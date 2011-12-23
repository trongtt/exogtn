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
package org.exoplatform.oauth.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * This class consists of following consumer information at least which are used by provider
 * to verify the consumer identity :<br>
 * <pre>
 * 1. Consumer Key
 * 2. Consumer Secret
 * 3. Callback URL
 * </pre>
 * 
 * Moreover, it possibly contains more information as properties to help administrators of provider
 * can know more about the consumer.
 * 
 * @author <a href="nguyenanhkien2a@gmail.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class Consumer
{
   private String consumerKey;

   private String consumerSecret;

   private String callbackURL;
   
   private final Map<String, String> properties = new HashMap<String, String>();

   public Consumer(String consumerKey, String consumerSecret, String callbackURL)
   {
      this.consumerKey = consumerKey;
      this.consumerSecret = consumerSecret;
      this.callbackURL = callbackURL;
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
    * @return the callbackURL
    */
   public String getCallbackURL()
   {
      return callbackURL;
   }

   /**
    * @param callbackURL the callbackURL to set
    */
   public void setCallbackURL(String callbackURL)
   {
      this.callbackURL = callbackURL;
   }

   public Object getProperty(String name)
   {
      return getProperties().get(name);
   }

   public void setProperty(String name, String value)
   {
      getProperties().put(name, value);
   }

   /**
    * @return the properties
    */
   public Map<String, String> getProperties()
   {
      return properties;
   }
}
