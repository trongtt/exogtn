/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package net.oauth.example.provider.servlets;

import net.oauth.OAuthProblemException;

import net.oauth.example.provider.core.OAuthServiceProvider;

import net.oauth.example.provider.core.ConsumerInfo;

import net.oauth.example.provider.core.RequestToken;

import net.oauth.example.provider.core.SimpleOAuthServiceProvider;

import net.oauth.example.provider.core.OAuthKeys;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.web.AbstractHttpServlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This Servlet is used to authorize request by validating username/password from user.
 * It will be called after OAuth request passed
 * 
 * See OAuth 1.0a specification for more detail
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class ExoAuthorizationServlet extends AbstractHttpServlet
{
   @Override
   protected void onService(ExoContainer container, HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {
      try
      {
         OAuthServiceProvider provider = (OAuthServiceProvider) ExoContainerContext.getCurrentContainer()
               .getComponentInstanceOfType(OAuthServiceProvider.class);
         OAuthMessage requestMessage = OAuthServlet.getMessage(request, null);

         RequestToken token = provider.getRequestToken(requestMessage.getToken());
         if (token == null)
         {
            throw new OAuthProblemException(OAuthKeys.OAUTH_TOKEN_EXPIRED);
         }
         
         //Initialize oauth_callback that get from consumer and mark it into token
         if(token.getProperty(OAuthKeys.OAUTH_CALLBACK) == null)
         {
            String callback = request.getParameter(OAuthKeys.OAUTH_CALLBACK);
            if (callback != null && callback.length() > 0)
            {
               token.setProperty(OAuthKeys.OAUTH_CALLBACK, callback);
            }            
         }

         // Token can has only request token and secret token.
         // If current Token was marked as authorized in some other way.
         ConsumerInfo consumer = provider.getConsumer(token.getConsumerKey());
         if (request.getParameter(OAuthKeys.OAUTH_AUTHORIZED) != null)
         {
            token.setProperty(OAuthKeys.OAUTH_AUTHORIZED, request.getParameter(OAuthKeys.OAUTH_AUTHORIZED).toString()
               .equals("Grant access"));
            returnToConsumer(request, response, consumer, token);
            return;
         }

         if (request.getRemoteUser() != null)
         {
            sendToAuthorizationPage(request, response, consumer, token);
         }
         else
         {
            sendToLoginPage(request, response, consumer, token);
         }
      }
      catch (Exception e)
      {
         SimpleOAuthServiceProvider.handleException(e, request, response, true);
      }
   }

   /**
    * Redirect to authorize page to make authorization again
    * @param request
    * @param response
    * @param accessor
    * @throws IOException
    * @throws ServletException
    */
   private void sendToLoginPage(HttpServletRequest request, HttpServletResponse response, ConsumerInfo consumer,
      RequestToken token) throws IOException, ServletException
   {
      String callbackURL =
         "http://localhost:8080/exo-oauth-provider/OAuthLoginCallback?oauth_token=" + token.getToken();
      String loginUrl =
         "http://localhost:8080/exo-oauth-login/ServiceLogin?callbackURL=" + callbackURL;

      response.sendRedirect(response.encodeRedirectURL(loginUrl));
   }

   /**
    * Redirect to consumer URL
    * @param request
    * @param response
    * @param accessor
    * @throws IOException
    * @throws ServletException
    */
   private void returnToConsumer(HttpServletRequest request, HttpServletResponse response, ConsumerInfo consumer,
         RequestToken token) throws IOException, ServletException
   {
      // send the user back to site's callBackUrl
      String callback = (String)token.getProperty(OAuthKeys.OAUTH_CALLBACK);
      if (callback == null && consumer.getCallbackUrl() != null && consumer.getCallbackUrl().length() > 0)
      {
         // first check if we have something from config
         callback = consumer.getCallbackUrl();
      }

      if (callback == null)
      {
         // no call back it must be a client
         response.setContentType("text/plain");
         PrintWriter out = response.getWriter();
         out.println("You have successfully authorized '" + consumer.getProperty("description")
               + "'. Please close this browser window and click continue" + " in the client.");
         out.close();
      }
      else
      {
         if (token != null)
         {
            if (Boolean.TRUE.equals(token.getProperty(OAuthKeys.OAUTH_AUTHORIZED)))
            {
               token.setUserId(request.getRemoteUser());
               callback = OAuth.addParameters(callback, OAuthKeys.OAUTH_TOKEN, token.getToken());
            }
            else
            {
               callback = OAuth.addParameters(callback, OAuthKeys.OAUTH_DENIED, token.getToken());
               ExoContainer container = getContainer();
               OAuthServiceProvider provider =
                     (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);
               provider.revokeRequestToken(token.getToken());
            }
         }

         response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
         response.setHeader("Location", callback);
      }
   }

   private void sendToAuthorizationPage(HttpServletRequest request, HttpServletResponse response,
      ConsumerInfo consumer, RequestToken token) throws IOException, ServletException
   {
      request.setAttribute(OAuthKeys.OAUTH_TOKEN, token.getToken());
      request.setAttribute(OAuthKeys.OAUTH_CONSUMER_NAME, consumer.getProperty("name"));
      request.getRequestDispatcher("login/jsp/authorize.jsp").forward(request, response);
   }

   private static final long serialVersionUID = 1L;

}
