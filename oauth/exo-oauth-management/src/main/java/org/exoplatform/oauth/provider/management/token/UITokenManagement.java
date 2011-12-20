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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.oauth.provider.management.token;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.oauth.provider.consumer.Consumer;
import org.exoplatform.oauth.provider.token.AccessToken;
import org.exoplatform.oauth.provider.OAuthServiceProvider;
import org.juzu.Action;
import org.juzu.Path;
import org.juzu.Response;
import org.juzu.View;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

public class UITokenManagement
{
   @Inject
   @Path("allTokens.gtmpl")
   org.exoplatform.oauth.provider.management.token.templates.allTokens allTokens;

   @View
   public void index()
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthServiceProvider provider =
         (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);

      Collection<AccessToken> tokens = provider.getAuthorizedTokens();
      Map<AccessToken, Consumer> accessors = new HashMap<AccessToken, Consumer>();
      for (AccessToken token : tokens)
      {
         if ("root".equals(token.getUserID()))
         {
            Consumer consumer = provider.getConsumer(token.getConsumerKey());
            accessors.put(token, consumer);
         }
      }

      allTokens.accessors(accessors).render();
   }

   @Action
   public void revokeAccessToken(String accessToken)
   {
      if (accessToken != null)
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         provider.revokeAccessToken(accessToken);
      }
   }
}
