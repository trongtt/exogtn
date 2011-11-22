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
package org.exoplatform.openid.core;

import org.chromattic.api.annotations.Create;
import org.chromattic.api.annotations.OneToMany;
import org.chromattic.api.annotations.PrimaryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Nov 18, 2011
 */
@PrimaryType(name = "nt:openidcontainer")
public abstract class OpenIDMapper
{
   @Create
   public abstract OpenIDEntry createOpenIDEntry();

   @OneToMany
   public abstract Map<String, OpenIDEntry> getOpenIds();

   public Collection<OpenIDEntry> getAllOpenId()
   {
      return getOpenIds().values();
   }

   public void saveOpenIdAccount(String openId, String username)
   {
      Map<String, OpenIDEntry> ids = getOpenIds();
      OpenIDEntry openIdEntry = ids.get(openId);
      if (openIdEntry == null)
      {
         openIdEntry = createOpenIDEntry();
         ids.put(openId, openIdEntry);
         openIdEntry.setUsername(username);
      }
   }

   public List<String> getOpenIdByUsername(String username)
   {
      Map<String, OpenIDEntry> openIds = getOpenIds();
      List<String> arrOpenIds = new ArrayList<String>();
      for (String openid : openIds.keySet())
      {
         if (openIds.get(openid).equals(username))
            arrOpenIds.add(openid);
      }

      return arrOpenIds;
   }

   public String getUsernameByOpenId(String openId)
   {
      Map<String, OpenIDEntry> openIds = getOpenIds();
      if (openIds != null)
      {
         OpenIDEntry entry = openIds.get(openId);
         if (entry != null)
         {
            return entry.getUsername();
         }
      }

      return null;
   }

   public void removeOpenID(String openId)
   {
      Map<String, OpenIDEntry> openIds = getOpenIds();
      openIds.remove(openId);
   }
}
