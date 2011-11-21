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

import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;

import java.io.IOException;

/**
 * This is a service to manage registration of consumers in OAuth Provider side
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public interface OAuthConsumerService
{
   /**
    * Return an OAuth Consumer which is mapped with specified consumerKey.
    * 
    * Return <tt>null</tt> if there isno consumer associated with this key.
    * 
    * @param consumerKey key as identifier of consumer
    * @return OAuthConsumer object
    * @throws IOException
    * @throws OAuthProblemException
    */
   public OAuthConsumer getConsumer(String consumerKey) throws OAuthProblemException;
   
   /**
    * Add an OAuth Consumer with specified key. If it does already contain an consumer for this key,
    * the old value will be replaced by the specified one.
    * 
    * @param consumerKey
    * @param consumer
    */
   public void addConsumer(String consumerKey, OAuthConsumer consumer);
}
