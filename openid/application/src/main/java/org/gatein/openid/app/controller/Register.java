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
package org.gatein.openid.app.controller;

import org.exoplatform.openid.OpenIdUtil;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.openid.app.OpenId;
import org.gatein.wci.security.Credentials;
import org.juzu.Action;
import org.juzu.Path;
import org.juzu.Response;
import org.juzu.View;
import org.juzu.impl.application.InternalApplicationContext;
import org.juzu.request.ActionContext;

import java.io.IOException;

import javax.inject.Inject;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Dec 13, 2011
 */
public class Register
{
   private Log _log = ExoLogger.getLogger("Register New User Process");

   @Inject
   @Path("register.gtmpl")
   org.gatein.openid.app.templates.register index;

   @Inject
   OpenId openId;

   @Inject
   Application app;

   @View
   public void index() throws IOException
   {
      if (openId.getOpenIdIdentifier() != null)
      {
         index.render();
      }
      else
      {
         app.index();
      }
   }

   @Action
   public Response register(String username, String password, String confirmPassword, String firstName,
         String lastName, String email)
   {
      User userData = new UserImpl(username);
      userData.setPassword(password);
      userData.setEmail(email);
      userData.setFirstName(firstName);
      userData.setLastName(lastName);
      return _processRegisterAccount(userData);
   }

   private Response _processRegisterAccount(User userData)
   {
      try
      {
         User user = OpenIdUtil.getOpenIDService().createUser(userData, openId.getOpenIdIdentifier());
         if (user != null)
         {
            //Auto Login
            String token = openId.getOpenIdToken();
            TransientTokenService tokenService = AbstractTokenService.getInstance(TransientTokenService.class);
            Credentials credentials = new Credentials(userData.getUserName(), token);
            token = tokenService.createToken(credentials);

            ActionContext actionCtx = (ActionContext) InternalApplicationContext.getCurrentRequest();
            return actionCtx.redirect("/portal/openidservlet?token=" + token);
         }
         _log.info("Create successfully user: " + user.getUserName());
      }
      catch (Exception e)
      {
         _log.error("Cannot create new user: ");
         e.printStackTrace();
      }

      return null;
   }
}
