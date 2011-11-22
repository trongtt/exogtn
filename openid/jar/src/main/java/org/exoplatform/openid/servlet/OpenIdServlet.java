/**
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.openid.servlet;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.web.AbstractHttpServlet;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.wci.security.Credentials;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Nov 24, 2011
 */
public class OpenIdServlet extends AbstractHttpServlet
{
   private static final long serialVersionUID = -631150770085187794L;

   private final Log log = ExoLogger.getLogger(OpenIdServlet.class);

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doPost(req, resp);
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      if (req.getRemoteUser() != null)
      {
         resp.sendRedirect("/portal");
      }

      String token = req.getParameter("token");
      TransientTokenService tokenService = AbstractTokenService.getInstance(TransientTokenService.class);
      Credentials tCredentials = tokenService.validateToken(token, false);

      if (tCredentials != null)
      {
         try
         {
            RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
            String userName = tCredentials.getUsername();
            Credentials credentials = new Credentials(tCredentials.getUsername(), token);
            OrganizationService orgService = (OrganizationService) getContainer().getComponentInstanceOfType(
                  OrganizationService.class);
            UserHandler userHandler = orgService.getUserHandler();
            User user = userHandler.findUserByName(userName);
            if (user != null)
            {
               try
               {
                  log.info("Make auto login");
                  req.getSession().setAttribute(Credentials.CREDENTIALS, credentials);
                  resp.sendRedirect("/portal/dologin");
               }
               catch (Exception e)
               {
                  log.error("authentication unsuccessful");
                  e.printStackTrace();
               }
            }
            else
            {
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         finally
         {
            RequestLifeCycle.end();
         }
      }
      else
      {
      }
   }
}
