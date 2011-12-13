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
package org.gatein.openid.app;

import org.juzu.SessionScoped;
import org.openid4java.discovery.DiscoveryInformation;

import java.io.Serializable;

import javax.inject.Named;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Dec 13, 2011
 */
@Named("Session")
@SessionScoped
public class Session implements Serializable
{

   private String returnUrl;

   private DiscoveryInformation discoveryInfo;

   private String openIdToken;

   private String openIdIdentifier;

   public void setReturnUrl(String returnUrl)
   {
      this.returnUrl = returnUrl;
   }

   public String getReturnUrl()
   {
      return this.returnUrl;
   }

   public void setDiscoveryInfo(DiscoveryInformation discovery)
   {
      this.discoveryInfo = discovery;
   }

   public DiscoveryInformation getDiscoveryInfo()
   {
      return discoveryInfo;
   }

   public void setOpenIdToken(String token)
   {
      this.openIdToken = token;
   }

   public String getOpenIdToken()
   {
      return openIdToken;
   }

   public void setOpenIdIdentifier(String identifier)
   {
      this.openIdIdentifier = identifier;
   }

   public String getOpenIdIdentifier()
   {
      return openIdIdentifier;
   }

}
