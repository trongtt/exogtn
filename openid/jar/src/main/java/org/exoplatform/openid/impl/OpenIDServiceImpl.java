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

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.openid.OpenIDDAO;
import org.exoplatform.openid.OpenIDService;
import org.exoplatform.openid.OpenIdUtil;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.impl.UserImpl;

import java.util.List;

/**
 * @author <a href="kien.nguyen@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class OpenIDServiceImpl implements OpenIDService
{
   private OpenIDDAO openIdDao;

   public OpenIDServiceImpl(OpenIDDAO openIDDAO)
   {
      openIdDao = openIDDAO;
   }

   public User findUserByOpenID(String openid)
   {
      try
      {
         String username = findUsernameByOpenID(openid);
         PortalContainer container = OpenIdUtil.getContainer();
         OrganizationService orgService = (OrganizationService) container
               .getComponentInstanceOfType(OrganizationService.class);
         User user = null;
         if (username != null)
         {
            begin(orgService);
            user = orgService.getUserHandler().findUserByName(username);
            end(orgService);

            return user;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public User createUser(User user, String openid) throws Exception
   {
      //TODO Need implement validator for register input fields
      //Save account into database
      PortalContainer container = OpenIdUtil.getContainer();
      OrganizationService orgService = (OrganizationService) container
            .getComponentInstanceOfType(OrganizationService.class);

      begin(orgService);
      UserHandler userHandler = orgService.getUserHandler();
      userHandler.createUser(user, true);
      end(orgService);

      this.mapToUser(openid, user.getUserName());

      return new UserImpl(user.getUserName());
   }

   public String findUsernameByOpenID(String openid)
   {
      return openIdDao.getUser(openid);
   }

   public void mapToUser(String openid, String username)
   {
      openIdDao.addOpenID(openid, username);
   }

   public void removeOpenID(String openId)
   {
      openIdDao.removeOpenId(openId);
   }

   public List<String> findOpenIdsByUser(String username)
   {
      return openIdDao.getOpenIds(username);
   }

   public List<String> getAllOpenIds()
   {
      return openIdDao.getAllOpenIds();
   }

   private void begin(OrganizationService orgService) throws Exception
   {
      if (orgService instanceof ComponentRequestLifecycle)
      {
         RequestLifeCycle.begin((ComponentRequestLifecycle) orgService);
      }
   }

   private void end(OrganizationService orgService) throws Exception
   {
      if (orgService instanceof ComponentRequestLifecycle)
      {
         RequestLifeCycle.end();
      }
   }

}
