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

import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.oauth.provider.Consumer;
import org.exoplatform.oauth.provider.OAuthServiceProvider;
import org.exoplatform.oauth.provider.OAuthToken;
import org.exoplatform.oauth.provider.RequestToken;
import org.exoplatform.oauth.provider.consumer.ConsumerEntry;
import org.exoplatform.oauth.provider.consumer.ConsumerStorage;
import org.exoplatform.oauth.provider.token.AccessTokenEntry;
import org.exoplatform.oauth.provider.token.AccessTokenStorage;
import org.exoplatform.services.organization.OrganizationService;
import org.picocontainer.Startable;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple implementation of OAuth Service Provider that provide details of persistent access token, consumer's information into JCR
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

   private ConsumerStorage consumerStorage;

   public SimpleOAuthServiceProvider(ChromatticManager manager, /* Pico is crap */OrganizationService orgService) throws Exception
   {
      this.tokenStorage = new AccessTokenStorage(manager);
      this.consumerStorage = new ConsumerStorage(manager, orgService);
   }

   public void start()
   {
      consumerStorage.start();
   }

   public void stop()
   {
      consumerStorage.stop();
   }

   /**
    * Get a consumer from consumer list
    * 
    * @param consumer_key key as identifier of consumer
    * @return OAuthConsumer object
    * @throws IOException
    * @throws OAuthProblemException
    */
   public Consumer getConsumer(String consumer_key)
   {
      ConsumerEntry inf = consumerStorage.getConsumer(consumer_key);

      if(inf == null)
      {
         return null;
      }
      else
      {
         return inf.getConsumer();
      }
   }

   public Consumer registerConsumer(String consumerKey, String consumerSecret, String callbackURL, Map<String, String> properties)
   {
      return consumerStorage.registerConsumer(consumerKey, consumerSecret, callbackURL, properties).getConsumer();
   }

   public void removeConsumer(String consumerKey)
   {
      ConsumerEntry consumer = consumerStorage.getConsumer(consumerKey);

      //TODO we need to improve how to store mapping of consumer and tokens to make easily for removing tokens, instead of looping
      if (consumer != null)
      {
         //Remove authorized tokens of this consumer
         Collection<OAuthToken> tokens = this.getAuthorizedTokens();
         for (OAuthToken t : tokens)
         {
            if (t.getConsumerKey().equals(consumer.getKey()))
            {
               this.revokeAccessToken(t.getToken());
            }
         }

         consumerStorage.deleteConsumer(consumerKey);
      }  
   }

   public List<Consumer> getAllConsumers()
   {
      List<Consumer> results = new ArrayList<Consumer>();
      Map<String, ConsumerEntry> consumerEntries = consumerStorage.getConsumerMap();
      for(String key : consumerEntries.keySet())
      {
         results.add(((ConsumerEntry)consumerEntries.get(key)).getConsumer());
      }
      return results;
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

   public OAuthToken generateAccessToken(RequestToken requestToken)
   {
      OAuthToken accessToken = generateAccessToken(requestToken.getUserId(), requestToken.getConsumerKey());
      if (accessToken != null)
      {
         revokeRequestToken(requestToken.getToken());
      }
      return accessToken;
   }
   
   public OAuthToken generateAccessToken(String userID, String consumerKey)
   {
      OAuthToken token = getAccessToken(userID, consumerKey);
      if(token != null)
      {
         return token;
      }
      return tokenStorage.generateAccessToken(userID, consumerKey).getOAuthToken();
   }

   public RequestToken getRequestToken(String token)
   {
      return requestTokens.get(token);
   }

   public OAuthToken getAccessToken(String key)
   {
      AccessTokenEntry tokenEntry = tokenStorage.getAccessToken(key);
      if (tokenEntry != null)
      {
         return tokenEntry.getOAuthToken();
      }
      return null;
   }

   public OAuthToken getAccessToken(String userID, String consumerKey)
   {
      AccessTokenEntry tokenEntry = tokenStorage.getAccessToken(userID, consumerKey);
      if (tokenEntry != null)
      {
         return tokenEntry.getOAuthToken();
      }
      return null;
   }

   public void revokeAccessToken(String token)
   {
      tokenStorage.removeAccessToken(token);
   }
   
   public void revokeRequestToken(String token)
   {
      requestTokens.remove(token);
   }

   public List<OAuthToken> getAuthorizedTokens()
   {
      Collection<AccessTokenEntry> tokenEntries = tokenStorage.getAccessTokens();
      List<OAuthToken> tokens = new ArrayList<OAuthToken>();
      for(AccessTokenEntry entry : tokenEntries)
      {
         tokens.add(entry.getOAuthToken());
      }
      return tokens;
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
