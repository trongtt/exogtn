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

import net.oauth.example.provider.core.RequestToken;

import net.oauth.example.provider.core.OAuthServiceProvider;

import net.oauth.example.provider.core.OAuthKeys;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.web.AbstractHttpServlet;
import org.exoplatform.web.security.security.TransientTokenService;

import java.io.IOException;

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
public class OAuthLoginCallback extends AbstractHttpServlet
{
   private static final long serialVersionUID = 1L;

   @Override
   protected void onService(ExoContainer container, HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
   {
      try
      {
         TransientTokenService tokenService =
            (TransientTokenService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               TransientTokenService.class);
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         String oauthToken = req.getParameter(OAuthKeys.OAUTH_TOKEN);
         String loginToken = req.getParameter("login_token");
         RequestToken reqToken = provider.getRequestToken(oauthToken);
         if (reqToken != null && loginToken != null && req.getRemoteUser() != null
            && tokenService.validateToken(loginToken, true) != null)
         {
            String authorizeURL = "/authorize?" + OAuthKeys.OAUTH_TOKEN + "=" + oauthToken;
            req.getRequestDispatcher(authorizeURL).forward(req, res);
         }
         else
         {
            throw new Exception("Get an error from authentication services");
         }
      }
      catch (Exception e)
      {
         //Should log this as information
         System.out.println(e.getMessage());
      }
   }
}
