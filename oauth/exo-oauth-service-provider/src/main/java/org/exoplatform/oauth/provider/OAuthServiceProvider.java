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
package org.exoplatform.oauth.provider;


import net.oauth.OAuthProblemException;
import org.exoplatform.oauth.provider.consumer.Consumer;
import org.exoplatform.oauth.provider.token.AccessToken;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * The OAuthServiceProvider defines an API to deal with
 *
 * 1. Consumer
 * 2. AccessToken
 * 3. RequestToken
 *
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public interface OAuthServiceProvider
{
   /**
    * Return a Consumer registered under key consumerKey or {@code null} if no such Consumer exists
    *
    * @param consumerKey
    * @return
    */
   public Consumer getConsumer(String consumerKey);

   /**
    * Register a Consumer with provided params: consumerKey, consumerSecret, callbackURL and properties
    *
    * In case another Consumer has been registered under the same consumerKey, this method overrides
    * former one.
    *
    * @param consumerKey
    * @param consumerSecret
    * @param callbackURL
    * @param properties
    */
   public Consumer registerConsumer(String consumerKey, String consumerSecret, String callbackURL, Map<String, String> properties);

   /**
    * Remove Consumer registered under key consumerKey. All AccessToken instances associated with this Consumer
    * must be clean from storage
    *
    * @param consumerKey
    */
   public void removeConsumer(String consumerKey);

   /**
    * Return all registered consumers
    *
    * @return
    */
   public Map<String, Consumer> getAllConsumers();
      
   /**
    * Generate request token from consumer information (name, key, etc)
    * request token is transient token
    * 
    * @return RequestToken
    */
   public RequestToken generateRequestToken(String consumerName);

   /**
    * This method is a short form of generateAccessToken(requestToken.getUserID(), requestToken.getConsumerKey())
    *
    * @param requestToken
    * @return
    */
   public AccessToken generateAccessToken(RequestToken requestToken);
   
   /**
    *
    * This method attempts first to find AccessToken associated with pair (userID, consumer)
    *
    * 1. If such AccessToken exists, the method returns it immediately
    *
    * 2. If no such AccessToken is found, the method generates an AccessToken from (userID, consumer)
    * then persists it
    *
    * @param userID
    * @param consumerKey
    * @return AccessToken
    */
   public AccessToken generateAccessToken(String userID, String consumerKey);
   
   /**
    * Get token information from token string
    * 
    * @param token
    * @return RequestToken
    */
   public RequestToken getRequestToken(String token);
   
   /**
    * Find an AccessToken stored under the token key: key and return it.
    * This method returns {@code null} if no such AccessToken is found
    *
    * @param key
    * @return AccessToken
    */
   public AccessToken getAccessToken(String key);

   /**
    * Find an AccessToken associated with the pair (userID, consumerKey) and return it.
    * This method returns {@code null} if no such AccessToken is found
    *
    * @param userID
    * @param consumerKey
    * @return
    */
   public AccessToken getAccessToken(String userID, String consumerKey);

   /**
    * Revoke access token
    * 
    * @param token
    * @throws OAuthProcessingException
    */
   public void revokeAccessToken(String token);
   
   /**
    * Revoke request token
    * 
    * @param token
    * @throws OAuthProcessingException
    */
   public void revokeRequestToken(String token);

   /**
    * Return a collection of all persistent AccessToken.
    * 
    * @return
    */
   public Collection<AccessToken> getAuthorizedTokens();
}
