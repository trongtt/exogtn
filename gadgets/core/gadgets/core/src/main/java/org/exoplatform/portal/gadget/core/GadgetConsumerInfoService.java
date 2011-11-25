/**
 * Copyright (C) 2009 eXo Platform SAS.
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

package org.exoplatform.portal.gadget.core;

import com.google.common.collect.Maps;

import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerIndex;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author <a href="mailto:khoi.nguyen@exoplatform.com">Nguyen Duc Khoi</a>  
 * Nov 25, 2010
 */
public class GadgetConsumerInfoService
{
   private Map<BasicOAuthStoreConsumerIndex, BasicOAuthStoreConsumerKeyAndSecret> consumerInfos;
   
   public GadgetConsumerInfoService()
   {
      consumerInfos = Maps.newHashMap();
   }
   
   public void addConsumerInfo(BasicOAuthStoreConsumerIndex providerKey, BasicOAuthStoreConsumerKeyAndSecret keyAndSecret)
   {
      consumerInfos.put(providerKey, keyAndSecret);
   }
   
   public void addConsumerInfo1(String consumerKey, String consumerSecret, String keyTypeStr, String callbackURL, String gadgetURIStr, String serviceName) throws URISyntaxException
   {
      URI gadgetURI = new URI(gadgetURIStr);
      BasicOAuthStoreConsumerIndex index = new BasicOAuthStoreConsumerIndex();
      index.setGadgetUri(gadgetURI.toASCIIString());
      index.setServiceName(serviceName);
      
      KeyType keyType = KeyType.HMAC_SYMMETRIC;
      
      if (keyTypeStr.equals("RSA_PRIVATE"))
      {
         keyType = KeyType.RSA_PRIVATE;
         consumerSecret = consumerSecret.replaceAll("-----[A-Z ]*-----", "").replace("\n", "");
      }
      BasicOAuthStoreConsumerKeyAndSecret kas = new BasicOAuthStoreConsumerKeyAndSecret(consumerKey, consumerSecret, keyType, null, callbackURL);
      
      consumerInfos.put(index, kas);
   }
   
   public BasicOAuthStoreConsumerKeyAndSecret getConsumerInfo(BasicOAuthStoreConsumerIndex pk)
   {
      return consumerInfos.get(pk);
   }

   /**
    * @param consumerInfos the consumerInfos to set
    */
   public void setConsumerInfos(Map<BasicOAuthStoreConsumerIndex, BasicOAuthStoreConsumerKeyAndSecret> consumerInfos)
   {
      this.consumerInfos = consumerInfos;
   }

   /**
    * @return the consumerInfos
    */
   public Map<BasicOAuthStoreConsumerIndex, BasicOAuthStoreConsumerKeyAndSecret> getConsumerInfos()
   {
      return consumerInfos;
   }
}
