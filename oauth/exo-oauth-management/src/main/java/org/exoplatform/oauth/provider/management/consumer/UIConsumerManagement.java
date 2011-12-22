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
 * You should have received org.exoplatform.oauth.provider.management copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.oauth.provider.management.consumer;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.oauth.provider.consumer.Consumer;
import org.exoplatform.oauth.provider.consumer.ConsumerProperty;
import org.exoplatform.oauth.provider.token.AccessToken;
import org.exoplatform.oauth.provider.OAuthServiceProvider;
import org.juzu.Action;
import org.juzu.Path;
import org.juzu.Response;
import org.juzu.View;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

public class UIConsumerManagement
{
   @Inject
   @Path("allConsumers.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.allConsumers allConsumers;

   @Inject
   @Path("consumerDetail.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.consumerDetail consumerDetail;

   @Inject
   @Path("addConsumer.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.addConsumer addConsumer;

   @Inject
   Session session;
   
   @View
   public void index()
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthServiceProvider oauthProvider =
         (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);
      allConsumers.consumers(oauthProvider.getAllConsumers()).render();
   }

   @View
   public void consumerDetail()
   {
      consumerDetail.session(session).render();
   }

   @View
   public void addConsumer()
   {
      addConsumer.session(session).render();
   }

   @Action
   public Response showConsumerDetail(String consumerKey)
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthServiceProvider oauthProvider =
         (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);
      Consumer consumer = oauthProvider.getConsumer(consumerKey);
      session.setConsumer(consumer);
      session.setAccessToken(oauthProvider.getAccessToken("root", consumerKey));
      return UIConsumerManagement_.consumerDetail();
   }

   @Action
   public Response showAddConsumer()
   {
      return UIConsumerManagement_.addConsumer();
   }

   @Action
   public Response deleteConsumer(String consumerKey)
   {
      if (consumerKey != null)
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         provider.removeConsumer(consumerKey);
      }
      return UIConsumerManagement_.index();
   }

   @Action
   public Response addNewConsumer(final String consumerKey, final String consumerSecret, final String callbackUrl, String consumerName,
                                  String consumerDescription, String consumerWebsite)
   {
      if(consumerKey == null || consumerSecret == null || callbackUrl == null)
      {
         StringBuilder message = new StringBuilder();
         message.append(consumerKey == null? "Consumer key is required" : "");
         message.append(consumerSecret == null ? "Consumer secret is required" : "");
         message.append(callbackUrl == null ? "Callback Url is required" : "");
         session.setMessage(message.toString());

         //Merely used from juzu template
         Consumer transientConsumer = new Consumer()
         {
            @Override
            public String getKey()
            {
               return consumerKey;
            }

            @Override
            public String getSecret()
            {
               return consumerSecret;
            }

            @Override
            public String getCallbackURL()
            {
               return callbackUrl;
            }

            @Override
            public ConsumerProperty createProperty()
            {
               return null;
            }

            @Override
            public Map<String, ConsumerProperty> getProperties()
            {
               return new HashMap<String, ConsumerProperty>();
            }

            @Override
            public void setKey(String key){}

            @Override
            public void setSecret(String secret){}

            @Override
            public void setCallbackURL(String callbackURL){}
         };
         session.setConsumer(transientConsumer);
         return UIConsumerManagement_.addConsumer();
      }
      else
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         Consumer consumer = provider.getConsumer(consumerKey);
         if (consumer != null)
         {
            StringBuilder message = new StringBuilder();
            message.append("Consumer Key is existing");
            session.setMessage(message.toString());
            session.setConsumer(consumer);
            return UIConsumerManagement_.addConsumer();
         }
         else
         {
            Map<String, String> consumerProperties = new HashMap<String, String>();
            consumerProperties.put("name", consumerName);
            consumerProperties.put("description", consumerDescription);
            consumerProperties.put("website", consumerWebsite);
            consumer = provider.registerConsumer(consumerKey, consumerSecret, callbackUrl, consumerProperties);
            session.setConsumer(consumer);
            return UIConsumerManagement_.consumerDetail();
         }
      }
   }

   @Action
   public Response createAccessToken(String consumerKey)
   {
      OAuthServiceProvider provider =
         (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
            OAuthServiceProvider.class);
      AccessToken token = provider.generateAccessToken( "root", consumerKey);
      session.setAccessToken(token);
      return UIConsumerManagement_.consumerDetail();
   }

}
