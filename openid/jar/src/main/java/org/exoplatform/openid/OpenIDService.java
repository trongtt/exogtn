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

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

import java.util.List;

/**
 * @author <a href="kien.nguyen@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public interface OpenIDService
{
   /**
    * Get all openId identifier linked to a username
    * 
    * @param username
    * @return List of openId identifier
    */
   public List<String> findOpenIdsByUser(String username);

   /**
    * Get all openid persisted in storage
    * @return
    */
   public List<String> getAllOpenIds();

   /**
    * Create a user via {@link OrganizationService} and link an openId identifier to created user
    * 
    * @param user
    * @param openid
    * @return
    * @throws Exception
    */
   public User createUser(User user, String openid) throws Exception;

   /**
    * Get username linked by openid identifier
    * 
    * @param openid
    * @return
    */
   public String findUsernameByOpenID(String openid);

   /**
    * Process map an openid identifier to a GateIn username
    * 
    * @param openId
    * @param username
    */
   public void mapToUser(String openId, String username);

   /**
    * Remove an openid identifier persisted in storage.
    * 
    * @param openId
    */
   public void removeOpenID(String openId);
}