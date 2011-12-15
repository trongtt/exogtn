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
package org.exoplatform.oauth.provider.consumer;

import org.chromattic.api.annotations.Create;
import org.chromattic.api.annotations.NamingPrefix;
import org.chromattic.api.annotations.OneToMany;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;
import java.util.Map;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 12/14/11
 */
@PrimaryType(name = "oauth:consumer")
@NamingPrefix(value = "oauth")
public abstract class Consumer
{
   @Property(name = "key")
   public abstract String getKey();

   public abstract void setKey(String key);

   @Property(name = "secret")
   public abstract String getSecret();

   public abstract void setSecret(String secret);

   @Property(name = "callbackURL")
   public abstract String getCallbackURL();

   public abstract void setCallbackURL(String callbackURL);

   @Create
   public abstract ConsumerProperty createProperty();

   @OneToMany
   public abstract Map<String, ConsumerProperty> getProperties();

}
