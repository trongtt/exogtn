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

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.wci.security.Credentials;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Nov 21, 2011
 */

public class OpenIdUtil
{
   /**
    * Get an instance of {@link ConsumerManager}, using for manage communication between OpenId consumer and provider
    * 
    * @return
    */
   public static ConsumerManager getConsumerManager()
   {
      _instance._initialize();
      return _instance._manager;
   }

   /**
    * Get an instance of {@link OpenIDService}
    * @return
    */
   public static OpenIDService getOpenIDService()
   {
      return (OpenIDService) getContainer().getComponentInstanceOfType(OpenIDService.class);
   }

   /**
    * Process login with username
    * <p>This util creates and persists a credential which used in login modules</p>
    * 
    * @param username
    * @param request
    * @param response
    * @throws IOException
    */
   public static void autoLogin(String username, ActionRequest request, ActionResponse response) throws IOException
   {
      String token = request.getPortletSession().getAttribute(OpenIdKeys.OPENID_TOKEN).toString();
      TransientTokenService tokenService = AbstractTokenService.getInstance(TransientTokenService.class);
      Credentials credentials = new Credentials(username, token);
      token = tokenService.createToken(credentials);

      response.sendRedirect("/portal/openidservlet?token=" + token);
   }

   public static PortalContainer getContainer()
   {
      return PortalContainer.getInstance();
   }

   private void _initialize()
   {
      try
      {
         if (_manager == null)
         {
            _manager = new ConsumerManager();
            _manager.setAssociations(new InMemoryConsumerAssociationStore());
            _manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
         }
      }
      catch (ConsumerException e)
      {
         _log.error(e.getMessage());
      }
   }

   private final Log _log = ExoLogger.getLogger(OpenIdUtil.class);

   private static OpenIdUtil _instance = new OpenIdUtil();

   private ConsumerManager _manager;
}
