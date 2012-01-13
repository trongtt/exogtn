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

package org.exoplatform.oauth.provider;

import net.oauth.OAuth.Problems;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.server.OAuthServlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.web.AbstractHttpServlet;
import org.exoplatform.oauth.provider.impl.SimpleOAuthServiceProvider;
import org.exoplatform.oauth.provider.validation.AccessorBuilder;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This Servlet will be called lastly after authentication and authorization passed successfully.
 * It creates an access token for getting resource
 * 
 * See OAuth 1.0a specification for more information
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class ExoAccessTokenServlet extends AbstractHttpServlet
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
         RequestToken token = provider.getRequestToken(requestMessage.getToken());
         if(token == null)
         {
            throw new OAuthProblemException(Problems.PERMISSION_DENIED);
         }
         
         //Verify secure string oauth_verifier that is created in authorization step
         if(token.getProperty(OAuthKeys.OAUTH_VERIFIER) != null)
         {
            String verifier = (String) token.getProperty(OAuthKeys.OAUTH_VERIFIER);
            if(!verifier.equals(requestMessage.getParameter(OAuthKeys.OAUTH_VERIFIER)))
            {
               throw new OAuthProblemException(OAuthKeys.OAUTH_VERIFIER_INVALID);
            }
            token.getProperties().remove(OAuthKeys.OAUTH_VERIFIER);
         }
         
         OAuthValidator validator = (OAuthValidator)container.getComponentInstanceOfType(OAuthValidator.class);
         validator.validateMessage(requestMessage, AccessorBuilder.buildAccessor(token, provider));

         // make sure token is authorized
         if (!Boolean.TRUE.equals(token.getProperty(OAuthKeys.OAUTH_AUTHORIZED)))
         {
            throw new OAuthProblemException(Problems.PERMISSION_DENIED);
         }

         OAuthToken accessToken = provider.generateAccessToken(token);

         res.setContentType("text/plain");
         OutputStream out = res.getOutputStream();
         OAuth.formEncode(
            OAuth.newList(OAuthKeys.OAUTH_TOKEN, accessToken.getToken(), OAuthKeys.OAUTH_TOKEN_SECRET, accessToken.getTokenSecret()), out);
         out.close();
      }
      catch (Exception e)
      {
         SimpleOAuthServiceProvider.handleException(e, req, res, true);
      }
   }
}
