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
package org.exoplatform.openid;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.security.jaas.DefaultLoginModule;
import org.exoplatform.web.security.security.TransientTokenService;

import javax.security.auth.login.LoginException;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Nov 17, 2011
 */
public class OpenIDLoginModule extends DefaultLoginModule
{
   @Override
   public boolean login() throws LoginException
   {
      try
      {
         String username = (String) sharedState.get("javax.security.auth.login.name");
         String password = (String) sharedState.get("javax.security.auth.login.password");
         if (username == null || password == null)
         {
            log.info("Cannot login with username and password are null");
            return false;
         }

         //Check token
         ExoContainer container = getContainer();
         Object o = ((TransientTokenService) container.getComponentInstanceOfType(TransientTokenService.class))
               .validateToken(password, true);
         if (o == null)
         {
            log.info("You are using GateIn Login Module");
            return false;
         }

         log.info("You are using OpenID Login Module");
         Authenticator authenticator = (Authenticator) getContainer().getComponentInstanceOfType(Authenticator.class);

         if (authenticator == null)
         {
            throw new LoginException("No Authenticator component found, check your configuration");
         }

         identity = authenticator.createIdentity(username);
         sharedState.put("exo.security.identity", identity);
         subject.getPublicCredentials().add(new UsernameCredential(username));

         //Temporarily reset username and password from sharedState to pass SharedStateLoginModule
         //TODO need checking auth_type in ShareStateLoginModule to have validate appropriately
         sharedState.put("javax.security.auth.login.name", null);
         sharedState.put("javax.security.auth.login.password", null);

         return true;
      }
      catch (final Throwable e)
      {
         log.error(e.getLocalizedMessage());
         throw new LoginException(e.getMessage());
      }
   }

   @Override
   public boolean commit() throws LoginException
   {
      return true;
   }

   public boolean abort() throws LoginException
   {
      return true;
   }

   public boolean logout() throws LoginException
   {
      return true;
   }
}
