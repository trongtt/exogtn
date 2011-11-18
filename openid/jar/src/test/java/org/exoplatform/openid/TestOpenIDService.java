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

import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.container.PortalContainer;

import java.util.List;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Nov 18, 2011
 */
public class TestOpenIDService extends AbstractOpenIDServiceTest
{
   protected ChromatticManager chromatticManager_;

   protected OpenIDService service_;

   @Override
   protected void setUp() throws Exception
   {
      PortalContainer container = PortalContainer.getInstance();
      chromatticManager_ = (ChromatticManager) container.getComponentInstanceOfType(ChromatticManager.class);
      service_ = (OpenIDService) container.getComponentInstanceOfType(OpenIDService.class);
      begin();
   }

   @Override
   protected void tearDown() throws Exception
   {
      chromatticManager_.getSynchronization().setSaveOnClose(false);
      end();
   }

   public void testSaveOpenID()
   {
      service_.mapToUser("openid", "username");
   }

   public void testGetAllOpenID()
   {
      int n = 10;
      for (int i = 0; i < n; i++)
      {
         service_.mapToUser("openid_" + i, "username_" + i);
      }
      List<String> ids = service_.getAllOpenIds();
      assertEquals(n, ids.size());
   }

   public void testGetOpenID()
   {
      String username = "user1";
      String openid_net = "http://foo.myopenid.com";
      assertGetOpenID(openid_net, username);

      String google_openid = "https://www.google.com/accounts/o8/id?id=AItOawmpS9ll-dxQhX_xHz4nyn2kjwsB5-SJfE8";
      assertGetOpenID(google_openid, username);

      String yahoo_openid = "https://me.yahoo.com/a/KV6f.4pkpt_6tynajIyZhWqcqBmmfJ2R6QhAPYM-#71d8b";
      assertGetOpenID(yahoo_openid, username);
   }

   private void assertGetOpenID(String openid, String username)
   {
      service_.mapToUser(openid, username);
      assertEquals(username, service_.findUsernameByOpenID(openid));
   }
}
