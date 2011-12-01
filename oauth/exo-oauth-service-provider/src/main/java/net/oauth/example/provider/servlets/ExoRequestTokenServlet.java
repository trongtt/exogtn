/*
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

import net.oauth.OAuth.Parameter;

import net.oauth.example.provider.core.TokenInfo;

import net.oauth.example.provider.core.OAuthKeys;

import net.oauth.example.provider.core.SimpleOAuthServiceProvider;

import net.oauth.OAuthProblemException;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.example.provider.core.OAuthServiceProvider;
import net.oauth.server.OAuthServlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.web.AbstractHttpServlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet process the first request, it validate information to avoid malform request or attacking
 * After validating completely, it create a token calls Request token for authorization step later
 * 
 * See OAuth 1.0a specification for more detail
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class ExoRequestTokenServlet extends AbstractHttpServlet
{
   private static final long serialVersionUID = 1L;
   
   @Override
   protected void onService(ExoContainer container, HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
   {
      try
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);

         OAuthMessage requestMessage = OAuthServlet.getMessage(req, null);
         OAuthConsumer consumer = SimpleOAuthServiceProvider.toOAuthConsumer(provider.getConsumer(requestMessage.getConsumerKey()));
         if (consumer == null)
         {
            OAuthProblemException problem =
               new OAuthProblemException(OAuthKeys.OAUTH_TOKEN_REJECTED + ": Consumer hasn't yet registered with provider");
            throw problem;
         }

         OAuthValidator validator = (OAuthValidator)container.getComponentInstanceOfType(OAuthValidator.class);
         validator.validateMessage(requestMessage, new OAuthAccessor(consumer));

         // generate request_token and secret
         TokenInfo token = provider.generateRequestToken(consumer.consumerKey);
         
         res.setContentType("text/plain");
         OutputStream out = res.getOutputStream();
         List<Parameter> params = OAuth.newList(OAuthKeys.OAUTH_TOKEN, token.getRequestToken(), OAuthKeys.OAUTH_TOKEN_SECRET, token.getTokenSecret());
         // Support the 'Variable Accessor Secret' extension
         // described in http://oauth.pbwiki.com/AccessorSecret
         String secret = requestMessage.getParameter(OAuthKeys.OAUTH_ACCESSOR_SECRET);
         if (secret != null)
         {
            params.add(new Parameter(OAuthKeys.OAUTH_ACCESSOR_SECRET, secret));
         }
         OAuth.formEncode(params, out);
         out.close();
      }
      catch (Exception e)
      {
         SimpleOAuthServiceProvider.handleException(e, req, res, true);
      }

   }
}
