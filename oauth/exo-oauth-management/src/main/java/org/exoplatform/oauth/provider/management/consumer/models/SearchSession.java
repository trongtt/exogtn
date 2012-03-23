/**
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.oauth.provider.management.consumer.models;

import org.exoplatform.oauth.provider.Consumer;
import org.juzu.SessionScoped;

import java.util.List;

import javax.inject.Named;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
@Named("searchSession")
@SessionScoped
public class SearchSession
{
   private String queryString;
   
   private String queryType;
   
   private List<Consumer> consumers;

   public String getQueryString()
   {
      return queryString;
   }

   public void setQueryString(String queryString)
   {
      this.queryString = queryString;
   }

   public String getQueryType()
   {
      return queryType;
   }

   public void setQueryType(String queryType)
   {
      this.queryType = queryType;
   }
   
   public List<Consumer> getConsumers()
   {
      return consumers;
   }

   public void setConsumers(List<Consumer> consumers)
   {
      this.consumers = consumers;
   }
}
