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
package org.exoplatform.openid.impl;

import org.chromattic.api.ChromatticSession;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.openid.OpenIDDAO;
import org.exoplatform.openid.core.OpenIDEntry;
import org.exoplatform.openid.core.OpenIDMapper;
import org.exoplatform.services.jcr.util.Text;
import org.picocontainer.Startable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Nov 18, 2011
 */
public class OpenIDDAOImpl implements Startable, OpenIDDAO
{
   private ChromatticLifeCycle chromatticLifeCycle;

   private String lifecycleName = "openid-mapping";

   public OpenIDDAOImpl(ChromatticManager chromatticManager)
   {
      this.chromatticLifeCycle = chromatticManager.getLifeCycle(lifecycleName);
   }

   private OpenIDMapper getOpenIDMapper()
   {
      ChromatticSession session = chromatticLifeCycle.getChromattic().openSession();
      OpenIDMapper mapper = session.findByPath(OpenIDMapper.class, lifecycleName);
      if (mapper == null)
      {
         mapper = session.insert(OpenIDMapper.class, lifecycleName);
      }
      return mapper;
   }

   @Override
   public String getUser(String openId)
   {
      openId = escapeOpenId(openId);
      OpenIDMapper mapper = getOpenIDMapper();
      return mapper.getUsernameByOpenId(openId);
   }

   @Override
   public void addOpenID(String openId, String username)
   {
      openId = escapeOpenId(openId);
      OpenIDMapper mapper = getOpenIDMapper();
      mapper.saveOpenIdAccount(openId, username);
   }

   @Override
   public void removeOpenId(String openId)
   {
      openId = escapeOpenId(openId);
      OpenIDMapper mapper = getOpenIDMapper();
      mapper.removeOpenID(openId);
   }

   @Override
   public List<String> getOpenIds(String username)
   {
      OpenIDMapper mapper = getOpenIDMapper();
      return mapper.getOpenIdByUsername(username);
   }

   @Override
   public List<String> getAllOpenIds()
   {
      List<String> openids = new ArrayList<String>();
      OpenIDMapper mapper = getOpenIDMapper();
      Collection<OpenIDEntry> ids = mapper.getAllOpenId();
      for (OpenIDEntry id : ids)
      {
         String openId = unescapeOpenId(id.getId());
         openids.add(openId);
      }

      return openids;
   }
   
   private String escapeOpenId(String openId)
   {
      if (openId == null) throw new IllegalArgumentException("The value must be set");
      return Text.escapeIllegalJcrChars(openId);
   }
   
   private String unescapeOpenId(String openId)
   {
      if (openId == null) throw new IllegalArgumentException("The value must be set");
      return Text.unescapeIllegalJcrChars(openId);
   }

   @Override
   public void start()
   {
   }

   @Override
   public void stop()
   {
   }

}
