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
package org.exoplatform.openid.listener;

import org.exoplatform.openid.OpenIDService;
import org.exoplatform.openid.OpenIdUtil;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

import java.util.List;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Nov 24, 2011
 */
public class OpenIdUserListener extends UserEventListener
{
   @Override
   public void postDelete(User user) throws Exception
   {
      OpenIDService service = OpenIdUtil.getOpenIDService();
      List<String> openids = service.findOpenIdsByUser(user.getUserName());
      for (String openid : openids)
      {
         service.removeOpenID(openid);
      }
   }
}
