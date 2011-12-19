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
package org.exoplatform.oauth.provider.impl;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;
import org.apache.commons.codec.digest.DigestUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.oauth.provider.ConsumerInfo;
import org.exoplatform.oauth.provider.OAuthServiceProvider;
import org.exoplatform.oauth.provider.OAuthToken;
import org.exoplatform.oauth.provider.RequestToken;
import org.exoplatform.oauth.provider.consumer.Consumer;
import org.exoplatform.oauth.provider.consumer.ConsumerStorage;
import org.exoplatform.oauth.provider.token.AccessToken;
import org.exoplatform.oauth.provider.token.AccessTokenStorage;
import org.picocontainer.Startable;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
   /* The request token should be transient and expired in short time */
   private Map<String, RequestToken> requestTokens = new HashMap<String, RequestToken>();

   private AccessTokenStorage tokenStorage;

   private ConsumerStorage consumerInfoStorage;

   public SimpleOAuthServiceProvider(AccessTokenStorage tokenStorage, ConsumerStorage consumerInfStorage) throws Exception
   {
      this.tokenStorage = tokenStorage;
      this.consumerInfoStorage = consumerInfStorage;
   }

   public void start()
   {
   }

   public void stop()
   {
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
      Consumer inf = consumerInfoStorage.getConsumer(consumer_key);

      if(inf == null)
      {
         return null;
      }
      else
      {
         return new ConsumerInfo(inf);
      }
   }

   public void addConsumer(ConsumerInfo consumer)
   {
      consumerInfoStorage.registerConsumer(consumer);
   }

   public void removeConsumer(String consumerKey)
   {
      consumerInfoStorage.deleteConsumer(consumerKey);
   }

   public Map<String, ConsumerInfo> getAllConsumers()
   {
      Map<String, ConsumerInfo> consumers = new LinkedHashMap<String, ConsumerInfo>();
      for(Consumer inf : consumerInfoStorage.getConsumers())
      {
         consumers.put(inf.getKey(), new ConsumerInfo(inf));
      }

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
      return tokenStorage.generateAccessToken(requestToken);
   }
   
   public AccessToken generateAccessToken(String userID, String consumerKey)
   {
      AccessToken token = getAccessToken(userID, consumerKey);
      if(token != null)
      {
         return token;
      }
      return tokenStorage.generateAccessToken(userID, consumerKey);
   }

   public RequestToken getRequestToken(String token)
   {
      return requestTokens.get(token);
   }

   public AccessToken getAccessToken(String key)
   {
      return tokenStorage.getAccessToken(key);
   }

   public AccessToken getAccessToken(String userID, String consumerKey)
   {
      return tokenStorage.getAccessToken(userID, consumerKey);
   }

   public void revokeAccessToken(String token)
   {
      tokenStorage.removeAccessToken(token);
   }
   
   public void revokeRequestToken(String token)
   {
      requestTokens.remove(token);
   }

   public Collection<AccessToken> getAuthorizedTokens()
   {
      return tokenStorage.getAccessTokens();
   }

   public static void handleException(Exception e, HttpServletRequest request, HttpServletResponse response,
      boolean sendBody) throws IOException, ServletException
   {
      String realm = (request.isSecure()) ? "https://" : "http://";
      realm += request.getLocalName();
      OAuthServlet.handleException(response, e, realm, sendBody);
   }
   
   /**
    * Create a secure random string
    * result is string that fit the ASCII letters 1-9, A-Z, a-z
    * 
    * @param length
    * @return
    */
   public static String createVerifier(int length)
   {
      final char[] CODEC = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
      byte[] bytes = new byte[length];
      new SecureRandom().nextBytes(bytes);
      char[] chars = new char[bytes.length];
      for (int i = 0; i < bytes.length; i++) {
         chars[i] = CODEC[((bytes[i] & 0xFF) % CODEC.length)];
      }
      return new String(chars);
   }
}
