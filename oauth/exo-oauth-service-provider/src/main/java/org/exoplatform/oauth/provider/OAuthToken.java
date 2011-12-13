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
package org.exoplatform.oauth.provider;

/**
 * The abstract token for both OAuth access and request tokens
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class OAuthToken
{
   private String consumerKey;

   private String tokenSecret;

   private String token;
   
   private String userId;

   /**
    * @return the consumerKey
    */
   public String getConsumerKey()
   {
      return consumerKey;
   }

   /**
    * @param consumerKey the consumerKey to set
    */
   public void setConsumerKey(String consumerKey)
   {
      this.consumerKey = consumerKey;
   }

   /**
    * @return the tokenSecret
    */
   public String getTokenSecret()
   {
      return tokenSecret;
   }

   /**
    * @param tokenSecret the tokenSecret to set
    */
   public void setTokenSecret(String tokenSecret)
   {
      this.tokenSecret = tokenSecret;
   }

   /**
    * @return the token
    */
   public String getToken()
   {
      return token;
   }

   /**
    * @param token the accessToken to set
    */
   public void setToken(String token)
   {
      this.token = token;
   }

   public String getUserId()
   {
      return userId;
   }

   public void setUserId(String userId)
   {
      this.userId = userId;
   }
}
