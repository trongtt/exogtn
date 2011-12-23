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
package org.exoplatform.oauth.provider.validation;

import net.oauth.OAuthConsumer;

import org.exoplatform.oauth.provider.Consumer;
import java.util.Map;

/**
 * Holder of static methods binding our OAuth data model with net.oauth lib 's
 *
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 12/19/11
 */
public class NetOAuthAdapter
{

   public static OAuthConsumer buildConsumer(Consumer consumer)
   {
      OAuthConsumer oauthConsumer = new OAuthConsumer(consumer.getCallbackURL(), consumer.getConsumerKey(), consumer.getConsumerSecret(), null);

      for(Map.Entry<String, String> property : consumer.getProperties().entrySet())
      {
         oauthConsumer.setProperty(property.getKey(), property.getValue());
      }
      return oauthConsumer;
   }
}
