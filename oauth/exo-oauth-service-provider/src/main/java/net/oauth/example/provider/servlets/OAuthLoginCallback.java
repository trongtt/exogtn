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
         String token = req.getParameter(OAuthKeys.OAUTH_TOKEN);
         OAuthServiceProvider provider = (OAuthServiceProvider) ExoContainerContext.getCurrentContainer()
               .getComponentInstanceOfType(OAuthServiceProvider.class);
         RequestToken reqToken = provider.getRequestToken(token);
         if (reqToken != null && req.getRemoteUser() != null)
         {
            String authorizeURL = "/authorize?" + OAuthKeys.OAUTH_TOKEN + "=" + token;
            req.getRequestDispatcher(authorizeURL).forward(req, res);
         }
         else
         {
            System.out.println("There is an error from authentication services");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
