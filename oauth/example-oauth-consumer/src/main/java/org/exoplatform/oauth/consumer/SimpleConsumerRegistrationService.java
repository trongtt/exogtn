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
package org.exoplatform.oauth.consumer;

import net.oauth.OAuthConsumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleConsumerRegistrationService implements ConsumerRegistrationService
{
   private final Map<ConsumerKey, ConsumerInfo> consumers = new HashMap<ConsumerKey, ConsumerInfo>();
   
   public static OAuthConsumer toOAuthConsumer(ConsumerInfo consumer) throws IOException
   {
      return null;
   }

   public ConsumerInfo getConsumer(ConsumerKey key)
   {
      return consumers.get(key);
   }

   public void addConsumer(ConsumerKey key, ConsumerInfo consumer)
   {
      consumers.put(key, consumer);
   }
}
