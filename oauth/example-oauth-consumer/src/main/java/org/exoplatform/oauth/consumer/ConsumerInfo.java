/*
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
package org.exoplatform.oauth.consumer;

/**
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class ConsumerInfo
{
   public static enum KeyType {
      HMAC_SYMMETRIC, RSA_PRIVATE
   }

   private String name;

   private String url;

   private String consumerKey;

   private String consumerSecret;

   private String callbackUrl;

   private KeyType keyType;

   public ConsumerInfo(String name, String url, String consumerKey, String consumerSecret, String callbackUrl,
      KeyType keyType)
   {
      this.name = name;
      this.url = url;
      this.consumerKey = consumerKey;
      this.consumerSecret = consumerSecret;
      this.callbackUrl = callbackUrl;
      this.keyType = keyType;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the url
    */
   public String getUrl()
   {
      return url;
   }

   /**
    * @param url the url to set
    */
   public void setUrl(String url)
   {
      this.url = url;
   }

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
    * @return the consumerSecret
    */
   public String getConsumerSecret()
   {
      return consumerSecret;
   }

   /**
    * @param consumerSecret the consumerSecret to set
    */
   public void setConsumerSecret(String consumerSecret)
   {
      this.consumerSecret = consumerSecret;
   }

   /**
    * @return the callbackUrl
    */
   public String getCallbackUrl()
   {
      return callbackUrl;
   }

   /**
    * @param callbackUrl the callbackUrl to set
    */
   public void setCallbackUrl(String callbackUrl)
   {
      this.callbackUrl = callbackUrl;
   }

   /**
    * @return the keyType
    */
   public KeyType getKeyType()
   {
      return keyType;
   }

   /**
    * @param keyType the keyType to set
    */
   public void setKeyType(KeyType keyType)
   {
      this.keyType = keyType;
   }
}
