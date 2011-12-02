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
package net.oauth.example.provider.core;


/**
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class AccessToken extends OAuthToken
{
   private String userId;

   public AccessToken()
   {
      this.userId = null;
   }

   @Override
   public AccessToken clone()
   {
      try
      {
         return (AccessToken)super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * @param userId the userId to set
    */
   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   /**
    * @return the userId
    */
   public String getUserId()
   {
      return userId;
   }
}
