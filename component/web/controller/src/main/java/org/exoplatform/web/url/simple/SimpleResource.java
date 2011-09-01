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
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 9/1/11
 */
public class SimpleResource
{
   private String handler;

   private String siteType;

   private String siteName;

   private String path;

   private final Map<QualifiedName, String> parameters = new HashMap<QualifiedName, String>();

   private Map<String, String[]> httpRequestParams = new HashMap<String, String[]>();

   public SimpleResource(String handler, String siteType, String siteName, String path, Map<QualifiedName, String> parameters, Map<String, String[]> httpRequestParams)
   {
      if(siteType == null || siteName == null)
      {
         throw new IllegalArgumentException("None of {siteType, siteName} could be null");
      }

      this.handler = handler;
      this.siteType = siteType;
      this.siteName = siteName;
      this.path = (path != null)? path : "";
      if (parameters != null)
      {
         this.parameters.putAll(parameters);
      }

      if (httpRequestParams != null)
      {
         this.httpRequestParams = httpRequestParams;
      }

      this.parameters.put(SimpleURL.INPUT_HANDLER, handler);
      this.parameters.put(SimpleURL.INPUT_SITE_TYPE, siteType);
      this.parameters.put(SimpleURL.INPUT_SITE_NAME, siteName);
      this.parameters.put(SimpleURL.INPUT_PATH, path);
   }

   public SimpleResource(String handler, String siteType, String siteName, String path)
   {
      this(handler, siteType, siteName, path, null, null);
   }

   public String getParameterValue(QualifiedName parameterName)
   {
      return parameters.get(parameterName);
   }

   public void setParameter(QualifiedName parameterName, String value)
   {
      if(value != null)
      {
         parameters.put(parameterName, value);
      }
   }

   public void setHandler(String handler)
   {
      setParameter(SimpleURL.INPUT_HANDLER, handler);
   }

   public void setSiteType(String siteType)
   {
      setParameter(SimpleURL.INPUT_SITE_TYPE, siteType);
   }

   public void setSiteName(String siteName)
   {
      setParameter(SimpleURL.INPUT_SITE_NAME, siteName);
   }

   public void setPath(String path)
   {
      setParameter(SimpleURL.INPUT_PATH, (path != null)? path : "");
   }

   public void setHttpRequestParams(Map<String, String[]> httpRequestParams)
   {
      this.httpRequestParams = httpRequestParams;
   }

   public Map<String, String[]> getHttpRequestParams()
   {
      return httpRequestParams;
   }
}
