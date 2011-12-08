/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package net.oauth.example.provider.core;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.exoplatform.container.ExoContainerContext;
import org.picocontainer.Startable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An simple OAuth Provider service which just persist everything in memory
 * 
 * Created by The eXo Platform SAS
 * Author : Nguyen Anh Kien
 *          nguyenanhkien2a@gmail.com
 * Dec 9, 2010  
 */
public class SimpleOAuthServiceProvider implements OAuthServiceProvider, Startable
{
   /*store all consumers into map with consumer key as identifier*/
   private final Map<String, ConsumerInfo> consumers = Collections
      .synchronizedMap(new HashMap<String, ConsumerInfo>(10));

   /* The access token should be persisted in DB */
   private Map<String, AccessToken> accessTokens = new HashMap<String, AccessToken>();

   /* The request token should be transient and expired in short time */
   private Map<String, RequestToken> requestTokens = new HashMap<String, RequestToken>();

   public void start()
   {
      try
      {
         loadConsumers();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   public void stop()
   {
   }

   /**
    * Load all consumers from a file and store them into memory
    * 
    * @throws IOException
    */
   private void loadConsumers() throws IOException
   {
      Properties p = new Properties();
      String resourceName = "consumer.properties";
      URL resource = this.getClass().getResource(resourceName);
      if (resource == null)
      {
         throw new IOException("resource not found: " + resourceName);
      }
      InputStream stream = resource.openStream();
      try
      {
         p.load(stream);
      }
      finally
      {
         stream.close();
      }

      // for each entry in the properties file create a OAuthConsumer
      for (Map.Entry<Object, Object> prop : p.entrySet())
      {
         String consumer_key = (String)prop.getKey();
         // make sure it's key not additional properties
         if (!consumer_key.contains("."))
         {
            String consumer_secret = (String)prop.getValue();
            if (consumer_secret != null)
            {
               String consumer_description = (String)p.getProperty(consumer_key + ".description");
               String consumer_callback_url = (String)p.getProperty(consumer_key + ".callbackURL");
               // Create OAuthConsumer w/ key and secret
               OAuthConsumer consumer = new OAuthConsumer(consumer_callback_url, consumer_key, consumer_secret, null);
               consumer.setProperty("name", consumer_key);
               consumer.setProperty("description", consumer_description);
               addConsumer(toConsumerInfo(consumer));
            }
         }
      }
   }

   /**
    * Get a consumer from consumer list
    * 
    * @param consumer_key key as identifier of consumer
    * @return OAuthConsumer object
    * @throws IOException
    * @throws OAuthProblemException
    */
   public ConsumerInfo getConsumer(String consumer_key)
   {
      try
      {
         // Try to reload consumers from the consumer.propreties.
         // It helps to be able to edit the file at runtime for testing
         loadConsumers();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return consumers.get(consumer_key);
   }

   public void addConsumer(ConsumerInfo consumer)
   {
      consumers.put(consumer.getConsumerKey(), consumer);
   }

   public void removeConsumer(String consumerKey)
   {
      consumers.remove(consumerKey);
   }

   public Map<String, ConsumerInfo> getAllConsumers()
   {
      return consumers;
   }

   public static ConsumerInfo toConsumerInfo(OAuthConsumer oauthConsumer)
   {
      ConsumerInfo consumer = new ConsumerInfo();
      consumer.setProperty("name", oauthConsumer.consumerKey);
      consumer.setConsumerKey(oauthConsumer.consumerKey);
      consumer.setConsumerSecret(oauthConsumer.consumerSecret);
      consumer.setCallbackUrl(oauthConsumer.callbackURL);
      return consumer;
   }

   public static OAuthConsumer toOAuthConsumer(ConsumerInfo consumer)
   {
      OAuthConsumer oauthConsumer =
         new OAuthConsumer(consumer.getCallbackUrl(), consumer.getConsumerKey(), consumer.getConsumerSecret(), null);
      oauthConsumer.setProperty("name", consumer.getProperty("name"));
      return oauthConsumer;
   }

   public RequestToken generateRequestToken(String consumerKey)
   {
      // generate oauth_token and oauth_secret from consumer key
      String token_data = consumerKey + System.nanoTime();
      String tokenMd5 = DigestUtils.md5Hex(token_data);

      String secret_data = consumerKey + System.nanoTime() + tokenMd5;
      String secretMd5 = DigestUtils.md5Hex(secret_data);

      RequestToken token = new RequestToken();
      token.setToken(tokenMd5);
      token.setTokenSecret(secretMd5);
      token.setConsumerKey(consumerKey);
      requestTokens.put(tokenMd5, token);
      return token;
   }

   public AccessToken generateAccessToken(RequestToken requestToken)
   {
      if (requestToken != null)
      {
         // remove request token
         requestTokens.remove(requestToken);

         // generate oauth_token and oauth_secret from consumer name (key)
         String token_data = requestToken.getConsumerKey() + System.nanoTime();
         String tokenMd5 = DigestUtils.md5Hex(token_data);

         String secret_data = requestToken.getConsumerKey() + System.nanoTime() + tokenMd5;
         String secretMd5 = DigestUtils.md5Hex(secret_data);

         AccessToken token = new AccessToken();
         token.setToken(tokenMd5);
         token.setConsumerKey(requestToken.getConsumerKey());
         token.setTokenSecret(secretMd5);
         token.setUserId(requestToken.getUserId());
         accessTokens.put(tokenMd5, token);
         return token;
      }
      return null;
   }

   public RequestToken getRequestToken(String token)
   {
      return requestTokens.get(token);
   }

   public AccessToken getAccessToken(String token)
   {
      return accessTokens.get(token);
   }

   public void revokeAccessToken(String token)
   {
      accessTokens.remove(token);
   }
   
   public void revokeRequestToken(String token)
   {
      requestTokens.remove(token);
   }

   public Collection<AccessToken> getAuthorizedTokens()
   {
      return accessTokens.values();
   }

   public static OAuthAccessor buildAccessor(OAuthToken token)
   {
      OAuthAccessor accessor = null;
      OAuthServiceProvider provider =
         (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
            OAuthServiceProvider.class);
      String consumerKey = token.getConsumerKey();

      if (consumerKey != null)
      {
         accessor = new OAuthAccessor(SimpleOAuthServiceProvider.toOAuthConsumer(provider.getConsumer(consumerKey)));
         if (token instanceof AccessToken)
         {
            accessor.accessToken = token.getToken();
         }
         else
         {
            accessor.requestToken = token.getToken();
         }
         accessor.tokenSecret = token.getTokenSecret();
      }
      else
      {
         //Should log this
      }

      return accessor;
   }

   public static void handleException(Exception e, HttpServletRequest request, HttpServletResponse response,
      boolean sendBody) throws IOException, ServletException
   {
      String realm = (request.isSecure()) ? "https://" : "http://";
      realm += request.getLocalName();
      OAuthServlet.handleException(response, e, realm, sendBody);
   }
   
   public static String getLoginCallbackURL()
   {
      return "http://localhost:8080/exo-oauth-provider/OAuthLoginCallback";
   }
}
