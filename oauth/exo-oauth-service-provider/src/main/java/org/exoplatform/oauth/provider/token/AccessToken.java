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

import org.chromattic.api.annotations.Name;
import org.chromattic.api.annotations.NamingPrefix;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 12/13/11
 */
@PrimaryType(name = "oauth:accesstoken")
@NamingPrefix(value = "oauth")
public abstract class AccessToken
{
   @Property(name = "consumerKey")
   public abstract String getConsumerKey();

   public abstract void setConsumerKey(String consumerKey);

   @Property(name = "tokenSecret")
   public abstract String getAccessTokenSecret();

   public abstract void setAccessTokenSecret(String tokenSecret);

   @Property(name = "accessTokenID")
   public abstract String getAccessTokenID();

   public abstract void setAccessTokenID(String accessTokenID);

   @Property(name = "userID")
   public abstract String getUserID();

   public abstract void setUserID(String userID);

}
