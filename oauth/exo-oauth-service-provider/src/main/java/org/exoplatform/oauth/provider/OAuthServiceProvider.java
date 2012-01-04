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

import java.util.List;
import java.util.Map;

/**
 * The OAuthServiceProvider defines an API to deal with
 *
 * 1. Consumer information
 * 2. Access Token
 * 3. Request Token
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
    * @return Consumer has this key
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
    * @throws OAuthException if has problem during register consumer, such as duplicate consumer key, etc
    * @return Consumer if register is successful or {@code null} if register is failure
    */
   public Consumer registerConsumer(String consumerKey, String consumerSecret, String callbackURL,
      Map<String, String> properties) throws OAuthException;

   /**
    * Remove Consumer registered under key consumerKey. All Access Token instances associated with this Consumer
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
   public List<Consumer> getAllConsumers();
      
   /**
    * Generate request token from consumer information (name or key)
    * request token is a transient token, it will be removed after AccessToken is created or configurable short time
    * 
    * @return RequestToken
    */
   public RequestToken generateRequestToken(String consumerKey);

   /**
    * This method is a short form of generateAccessToken(requestToken.getUserID(), requestToken.getConsumerKey())
    * 
    * @param requestToken
    * @return
    */
   public OAuthToken generateAccessToken(RequestToken requestToken);
   
   /**
    *
    * This method attempts first to find Access Token associated with pair (userID, consumer)
    *
    * 1. If such AccessTokenEntry exists, the method returns it immediately
    *
    * 2. If no such Access Token is found, the method generates an Access Token from (userID, consumer)
    * then persists it
    *
    * @param userID
    * @param consumerKey
    * @return OAuthToken is Access Token
    */
   public OAuthToken generateAccessToken(String userID, String consumerKey);
   
   /**
    * Get token information from token string
    * 
    * @param token
    * @return RequestToken
    */
   public RequestToken getRequestToken(String token);
   
   /**
    * Find an Access Token stored under the token string: token and return it.
    * This method returns {@code null} if no such Access Token is found
    *
    * @param key
    * @return OAuthToken is Access Token
    */
   public OAuthToken getAccessToken(String token);

   /**
    * Find an Access Token associated with the pair (userID, consumerKey) and return it.
    * This method returns {@code null} if no such Access Token is found
    *
    * @param userID
    * @param consumerKey
    * @return
    */
   public OAuthToken getAccessToken(String userID, String consumerKey);

   /**
    * Revoke access token
    * 
    * @param token
    */
   public void revokeAccessToken(String token);
   
   /**
    * Revoke request token
    * 
    * @param token
    */
   public void revokeRequestToken(String token);

   /**
    * Return a list of all persistent Access Token.
    * 
    * @return
    */
   public List<OAuthToken> getAccessTokens();
}
