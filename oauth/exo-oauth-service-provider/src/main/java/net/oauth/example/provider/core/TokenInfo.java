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

import java.util.HashMap;
import java.util.Map;

/**
 * This class consists information about OAuth Token <br>
 * <pre>
 * 1. token
 * 2. consumer key
 * 3. user Id
 * 4. properties map contain extra information: expiration time, etc
 * </pre>
 * 
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

public class TokenInfo implements Cloneable
{
   private String requestToken;
   private String accessToken;
   private String tokenSecret;
   private String consumerKey;
   private String userId;
   
   private final Map<String, Object> properties = new HashMap<String, Object>();

   public TokenInfo() {
       this.requestToken = null;
       this.accessToken = null;
       this.consumerKey = null;
       this.userId = null;
   }
   
   @Override
   public TokenInfo clone()
   {
      try {
         return (TokenInfo) super.clone();
     } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
     }
   }

   /**
    * @param consumerKey the consumerKey to set
    */
   public void setConsumerKey(String consumerKey)
   {
      this.consumerKey = consumerKey;
   }

   /**
    * @return the consumerKey
    */
   public String getConsumerKey()
   {
      return consumerKey;
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
   
   public Object getProperty(String name)
   {
      return properties.get(name);
   }

   public void setProperty(String name, Object value)
   {
      properties.put(name, value);
   }

   /**
    * @return the properties
    */
   public Map<String, Object> getProperties()
   {
      return properties;
   }

   /**
    * @param requestToken the requestToken to set
    */
   public void setRequestToken(String requestToken)
   {
      this.requestToken = requestToken;
   }

   /**
    * @return the requestToken
    */
   public String getRequestToken()
   {
      return requestToken;
   }

   /**
    * @param accessToken the accessToken to set
    */
   public void setAccessToken(String accessToken)
   {
      this.accessToken = accessToken;
   }

   /**
    * @return the accessToken
    */
   public String getAccessToken()
   {
      return accessToken;
   }

   /**
    * @param tokenSecret the tokenSecret to set
    */
   public void setTokenSecret(String tokenSecret)
   {
      this.tokenSecret = tokenSecret;
   }

   /**
    * @return the tokenSecret
    */
   public String getTokenSecret()
   {
      return tokenSecret;
   }
}
