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

package org.exoplatform.oauth.provider;

import net.oauth.OAuthProblemException;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.web.AbstractHttpServlet;
import org.exoplatform.oauth.provider.impl.SimpleOAuthServiceProvider;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.wci.security.Credentials;

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
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         OAuthMessage requestMessage = OAuthServlet.getMessage(request, null);

         RequestToken token = provider.getRequestToken(requestMessage.getToken());
         if (token == null)
         {
            throw new OAuthProblemException(OAuthKeys.OAUTH_TOKEN_EXPIRED);
         }

         //Initialize oauth_callback that get from consumer and mark it into token
         if (token.getProperty(OAuthKeys.OAUTH_CALLBACK) == null)
         {
            String callback = request.getParameter(OAuthKeys.OAUTH_CALLBACK);
            if (callback != null && callback.length() > 0)
            {
               token.setProperty(OAuthKeys.OAUTH_CALLBACK, callback);
            }
         }

         // Token can has only request token and secret token.
         // If current Token was marked as authorized in some other way.
         Consumer consumer = provider.getConsumer(token.getConsumerKey());
         if (request.getParameter(OAuthKeys.OAUTH_AUTHORIZED) != null)
         {
            token.setProperty(OAuthKeys.OAUTH_AUTHORIZED, request.getParameter(OAuthKeys.OAUTH_AUTHORIZED).toString()
               .equals("Grant access"));
            returnToConsumer(request, response, consumer, token);
            return;
         }

         //Verify authentication and authorization
         TransientTokenService tokenService =
            (TransientTokenService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               TransientTokenService.class);
         String loginToken = request.getParameter(OAuthKeys.OAUTH_LOGIN_TOKEN);
         if (loginToken != null)
         {
            Credentials credentials = tokenService.validateToken(loginToken, true);
            if (credentials != null)
            {
               token.setUserId(credentials.getUsername());
               sendToAuthorizationPage(request, response, consumer, token);
            }
            else
            {
               token.setProperty(OAuthKeys.OAUTH_AUTHORIZED, false);
               returnToConsumer(request, response, consumer, token);
            }
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
    * @throws IOException
    * @throws ServletException
    */
   private void sendToLoginPage(HttpServletRequest request, HttpServletResponse response, Consumer consumer,
      RequestToken token) throws IOException, ServletException
   {
      String callbackURL = request.getRequestURL().toString();
      String localhost =
         request.getScheme() + "://" + request.getServerName()
            + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
      String portal = ExoContainerContext.getCurrentContainer().getContext().getPortalContainerName();
      String loginCtx = "/OAuthLogin";
      String loginUrl =
         localhost + "/" + portal + loginCtx + "?callback=" + callbackURL + "&oauth_token=" + token.getToken();;

      response.sendRedirect(response.encodeRedirectURL(loginUrl));
   }

   /**
    * Redirect to consumer URL
    * @param request
    * @param response
    * @throws IOException
    * @throws ServletException
    */
   private void returnToConsumer(HttpServletRequest request, HttpServletResponse response, Consumer consumer,
      RequestToken token) throws IOException, ServletException
   {
      // send the user back to site's callBackUrl
      String callback = (String)token.getProperty(OAuthKeys.OAUTH_CALLBACK);
      if (callback == null)
      {
         // first check if we have something from config
         callback = consumer.getCallbackURL();
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
               final String verifier = null;//SimpleOAuthServiceProvider.createVerifier(10);
               token.setProperty(OAuthKeys.OAUTH_VERIFIER, verifier);
               callback =
                  OAuth.addParameters(callback, OAuthKeys.OAUTH_TOKEN, token.getToken(), OAuthKeys.OAUTH_VERIFIER,
                     verifier);
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
      Consumer consumer, RequestToken token) throws IOException, ServletException
   {
      request.setAttribute(OAuthKeys.OAUTH_TOKEN, token.getToken());
      request.setAttribute(OAuthKeys.OAUTH_CONSUMER_NAME, consumer.getProperty("name"));
      request.getRequestDispatcher("authorize/jsp/authorize.jsp").forward(request, response);
   }

   private static final long serialVersionUID = 1L;

}
