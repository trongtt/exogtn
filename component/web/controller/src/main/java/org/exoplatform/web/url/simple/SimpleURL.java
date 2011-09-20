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
package org.exoplatform.web.url.simple;

import org.exoplatform.web.controller.QualifiedName;
import org.exoplatform.web.url.PortalURL;
import org.exoplatform.web.url.ResourceType;
import org.exoplatform.web.url.URLContext;
import org.exoplatform.web.url.navigation.NavigationResource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 9/1/11
 */
public class SimpleURL extends PortalURL<NavigationResource, SimpleURL>
{

   public static final ResourceType<NavigationResource, SimpleURL> TYPE = new ResourceType<NavigationResource, SimpleURL>(){};

   public static final QualifiedName SITE_TYPE = QualifiedName.create("gtn", "sitetype");

   public static final QualifiedName SITE_NAME = QualifiedName.create("gtn", "sitename");

   public static final QualifiedName LANG = QualifiedName.create("gtn", "lang");

   public static final QualifiedName PATH = QualifiedName.create("gtn", "path");

   public static final Set<QualifiedName> PARAMETER_NAMES = new HashSet<QualifiedName>();

   static
   {
      PARAMETER_NAMES.add(SITE_TYPE);
      PARAMETER_NAMES.add(SITE_NAME);
      PARAMETER_NAMES.add(PATH);
   }

   private NavigationResource resource;

   public SimpleURL(URLContext context) throws NullPointerException
   {
      super(context);
   }

   @Override
   public NavigationResource getResource()
   {
      return resource;
   }

   @Override
   public SimpleURL setResource(NavigationResource resource)
   {
      this.resource = resource;
      return this;
   }

   @Override
   public Set<QualifiedName> getParameterNames()
   {
      return PARAMETER_NAMES;
   }

   @Override
   public String getParameterValue(QualifiedName parameterName)
   {
      if(SITE_TYPE.equals(parameterName))
      {
         return resource.getSiteType().getName();
      }
      else if(SITE_NAME.equals(parameterName))
      {
         return resource.getSiteName();
      }
      else if(PATH.equals(parameterName))
      {
         return resource.getNodeURI();
      }
      else
      {
         return null;
      }
   }
}
