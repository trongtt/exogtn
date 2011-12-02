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

import net.oauth.OAuthProblemException;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * The OAuth Provider services responsibility is to allow Consumer Developers
 * to establish a Consumer Key and Consumer Secret.
 * <p>
 * The process and requirements for provisioning these are entirely up to the Service Providers.
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public interface OAuthServiceProvider
{
   /**
    * Return an OAuth ConsumerInfo which is mapped with specified consumerKey.
    * 
    * Return <tt>null</tt> if there is no consumer associated with this key.
    * 
    * @param consumerKey key as identifier of consumer
    * @return OAuthConsumer object
    * @throws IOException
    * @throws OAuthProblemException
    */
   public ConsumerInfo getConsumer(String consumerKey);

   /**
    * Add an OAuth ConsumerInfo. If it does already contain an consumer for this key,
    * the old value will be replaced by the specified one.
    * 
    * @param consumerKey
    * @param consumer
    */
   public void addConsumer(ConsumerInfo consumer);

   /**
    * Remove the OAuth ConsumerInfo with specified key
    * @param consumerKey
    */
   public void removeConsumer(String consumerKey);
   
   /**
    * Return all consumers
    * @return
    */
   public Map<String, ConsumerInfo> getAllConsumers();
      
   /**
    * Generate request token from consumer information (name, key, etc)
    * request token is temporal token
    * 
    * @param consumer
    * @return RequestToken
    */
   public RequestToken generateRequestToken(String consumerName);

   /**
    * Generate token from request token
    * 
    * @param requestToken
    * @return RequestToken
    */
   public AccessToken generateAccessToken(RequestToken requestToken);
   
   /**
    * Get token information from token string
    * 
    * @param token
    * @return RequestToken
    */
   public RequestToken getRequestToken(String token);
   
   /**
    * Get token information from token string
    * 
    * @param token
    * @return RequestToken
    */
   public AccessToken getAccessToken(String token);

   /**
    * Revoke token
    * 
    * @param token
    * @throws OAuthProcessingException
    */
   public void revokeAccessToken(String token);

   /**
    * Return all OAuth authorized tokens in system
    * 
    * @return
    */
   public Collection<AccessToken> getAuthorizedTokens();
}
