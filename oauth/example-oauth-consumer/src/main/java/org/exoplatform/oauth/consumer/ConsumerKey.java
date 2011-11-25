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
package org.exoplatform.oauth.consumer;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class ConsumerKey implements Serializable
{
   private final String name;

   private final String url;

   public ConsumerKey(String name, String url)
   {
      this.name = name;
      this.url = url;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the url
    */
   public String getUrl()
   {
      return url;
   }

   @Override
   public int hashCode()
   {
      return hashCode(name, url);
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final ConsumerKey other = (ConsumerKey)obj;
      if (name == null)
      {
         if (other.name != null)
         {
            return false;
         }
      }
      else if (!name.equals(other.name))
      {
         return false;
      }
      if (url == null)
      {
         if (other.url != null)
         {
            return false;
         }
      }
      else if (!url.equals(other.url))
      {
         return false;
      }
      return true;
   }

   private int hashCode(Object... objects)
   {
      return Arrays.hashCode(objects);
   }
}