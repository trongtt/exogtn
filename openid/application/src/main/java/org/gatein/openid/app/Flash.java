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

import org.juzu.FlashScoped;
import org.openid4java.discovery.DiscoveryInformation;

import javax.inject.Named;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Dec 13, 2011
 */
@Named("flash")
@FlashScoped
public class Flash
{
   private DiscoveryInformation discover;

   private String returnUrl;

   public void setDiscover(DiscoveryInformation discover)
   {
      this.discover = discover;
   }

   public DiscoveryInformation getDiscover()
   {
      return discover;
   }

   public void setReturnUrl(String returnUrl)
   {
      this.returnUrl = returnUrl;
   }

   public String getReturnUrl()
   {
      return returnUrl;
   }

}
