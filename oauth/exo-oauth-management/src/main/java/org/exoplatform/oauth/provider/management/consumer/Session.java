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
package org.exoplatform.oauth.provider.management.consumer;

import org.exoplatform.oauth.provider.token.AccessToken;
import org.exoplatform.oauth.provider.Consumer;
import org.juzu.SessionScoped;

import java.io.Serializable;

import javax.inject.Named;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
@Named("session")
@SessionScoped
public class Session implements Serializable
{
   private static final long serialVersionUID = 1L;

   private Consumer consumer;

   private AccessToken accessToken;

   private String message;

   private String currentUser;

   /**
    * @param consumer the consumers to set
    */
   public void setConsumer(Consumer consumer)
   {
      this.consumer = consumer;
   }

   /**
    * @return the consumers
    */
   public Consumer getConsumer()
   {
      return consumer;
   }

   /**
    * @param accessToken the token to set
    */
   public void setAccessToken(AccessToken accessToken)
   {
      this.accessToken = accessToken;
   }

   /**
    * @return the token
    */
   public AccessToken getAccessToken()
   {
      return accessToken;
   }

   /**
    * @param message the message to set
    */
   public void setMessage(String message)
   {
      this.message = message;
   }

   /**
    * @return the message
    */
   public String getMessage()
   {
      return message;
   }

   /**
    * @param currentUser the currentUser to set
    */
   public void setCurrentUser(String currentUser)
   {
      this.currentUser = currentUser;
   }

   /**
    * @return the currentUser
    */
   public String getCurrentUser()
   {
      return currentUser;
   }

}
