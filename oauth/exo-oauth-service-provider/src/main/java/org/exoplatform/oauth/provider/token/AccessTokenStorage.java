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
package org.exoplatform.oauth.provider.token;

import org.apache.commons.codec.digest.DigestUtils;
import org.chromattic.api.ChromatticSession;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.oauth.provider.RequestToken;
import org.picocontainer.Startable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 12/13/11
 */
public class AccessTokenStorage implements Startable
{

   private ChromatticLifeCycle lifecycle;

   public AccessTokenStorage(ChromatticManager manager) throws Exception
   {
      lifecycle = manager.getLifeCycle("oauth");
   }

   public void start()
   {
   }

   private AccessTokenContainer getTokenContainer()
   {
      ChromatticSession session = lifecycle.getContext().getSession();
      AccessTokenContainer tokenContainer = session.findByPath(AccessTokenContainer.class, "accesstokens");
      if(tokenContainer == null)
      {
         synchronized (this)
         {
            if((tokenContainer = session.findByPath(AccessTokenContainer.class, "accesstokens")) == null)
            {
               tokenContainer = session.insert(AccessTokenContainer.class, "accesstokens");
            }
         }
      }
      return tokenContainer;
   }

   public AccessToken generateAccessToken(RequestToken requestToken)
   {
      AccessTokenContainer tokenContainer = getTokenContainer();
      AccessToken token = tokenContainer.createAccessToken();
      String tokenID = DigestUtils.md5Hex(requestToken.getConsumerKey() + System.nanoTime());
      String tokenSecret = DigestUtils.md5Hex(requestToken.getConsumerKey() + System.nanoTime() + tokenID);
      tokenContainer.getAccessTokens().put(tokenID, token);

      token.setAccessTokenID(tokenID);
      token.setConsumerKey(requestToken.getConsumerKey());
      token.setAccessTokenSecret(tokenSecret);
      token.setUserID(requestToken.getUserId());
      return token;
   }

   public AccessToken getAccessToken(String tokenId)
   {
      if(tokenId == null)
      {
         return null;
      }
      else
      {
         return getTokenContainer().getAccessTokens().get(tokenId);
      }
   }

   /**
    * This method is needed to avoid generating multiple AccessToken for
    * a pair user/consumer in case user uses different browsers or loses
    * cookie
    *
    * @param userID
    * @param consumerKey
    * @return
    */
   public AccessToken getAccessToken(String userID, String consumerKey)
   {
      Collection<AccessToken> allTokens = getAccessTokens();
      if(allTokens != null)
      {
         for(AccessToken token : allTokens)
         {
            if(token.getUserID().equals(userID) && token.getConsumerKey().equals(consumerKey))
            {
               return token;
            }
         }
      }
      return null;
   }

   public Collection<AccessToken> getAccessTokens()
   {
      return Collections.unmodifiableCollection(getTokenContainer().getAccessTokens().values());
   }

   public void removeAccessToken(String tokenId)
   {
      if(tokenId != null)
      {
         getTokenContainer().getAccessTokens().remove(tokenId);
      }
   }

   public void stop()
   {
   }
}
