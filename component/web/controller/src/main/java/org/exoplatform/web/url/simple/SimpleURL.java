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
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 9/1/11
 */
public class SimpleURL extends PortalURL<SimpleResource, SimpleURL>
{

   public static final ResourceType<SimpleResource, SimpleURL> TYPE = new ResourceType<SimpleResource, SimpleURL>(){};

   public static final QualifiedName INPUT_HANDLER = QualifiedName.create("gtn", "handler");

   public static final QualifiedName INPUT_SITE_TYPE = QualifiedName.create("gtn", "sitetype");

   public static final QualifiedName INPUT_SITE_NAME = QualifiedName.create("gtn", "sitename");

   public static final QualifiedName INPUT_PATH = QualifiedName.create("gtn", "path");

   public static final QualifiedName INPUT_LANG = QualifiedName.create("gtn", "lang");

   public static final Set<QualifiedName> PARAMETER_NAMES = new HashSet<QualifiedName>();

   static
   {
      PARAMETER_NAMES.add(INPUT_HANDLER);
      PARAMETER_NAMES.add(INPUT_SITE_TYPE);
      PARAMETER_NAMES.add(INPUT_SITE_NAME);
      PARAMETER_NAMES.add(INPUT_PATH);
      PARAMETER_NAMES.add(INPUT_LANG);
   }

   private SimpleResource resource;

   public SimpleURL(URLContext context) throws NullPointerException
   {
      super(context);
   }

   @Override
   public SimpleResource getResource()
   {
      return resource;
   }

   @Override
   public SimpleURL setResource(SimpleResource resource)
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
      return resource.getParameterValue(parameterName);
   }
}
