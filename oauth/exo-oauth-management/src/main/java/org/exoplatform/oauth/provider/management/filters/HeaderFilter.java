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
 * You should have received org.exoplatform.oauth.provider.management copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.oauth.provider.management.filters;

import org.w3c.dom.Element;

import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import java.io.IOException;

/**
 * @author <org.exoplatform.oauth.provider.management href="kienna@exoplatform.com">Kien Nguyen</org.exoplatform.oauth.provider.management>
 * @version $Revision$
 */

public class HeaderFilter implements RenderFilter
{

   public void init(FilterConfig filterConfig) throws PortletException
   {
   }

   public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException,
      PortletException
   {
      Element cssElement = response.createElement("link");
      cssElement.setAttribute("href", response.encodeURL(request.getContextPath() + "/skin/OAuthStyle.css"));
      cssElement.setAttribute("rel", "stylesheet");
      cssElement.setAttribute("type", "text/css");

      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, cssElement);

      chain.doFilter(request, response);
   }

   public void destroy()
   {
   }
}
