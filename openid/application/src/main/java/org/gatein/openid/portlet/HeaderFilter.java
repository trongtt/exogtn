/**
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
package org.gatein.openid.portlet;

import org.w3c.dom.Element;

import java.io.IOException;

import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Dec 12, 2011
 */
public class HeaderFilter implements RenderFilter
{

   @Override
   public void init(FilterConfig filterConfig) throws PortletException
   {

   }

   @Override
   public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException, PortletException
   {
      // add CSS
      Element cssElement = response.createElement("link");
      cssElement.setAttribute("href", response.encodeURL((request.getContextPath() + "/public-resources/stylesheet/openid.css")));
      cssElement.setAttribute("rel", "stylesheet");
      cssElement.setAttribute("type", "text/css");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, cssElement);

      Element cssSelectorElement = response.createElement("link");
      cssSelectorElement.setAttribute("href", response.encodeURL((request.getContextPath() + "/public-resources/stylesheet/openid-selector.css")));
      cssSelectorElement.setAttribute("rel", "stylesheet");
      cssSelectorElement.setAttribute("type", "text/css");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, cssSelectorElement);

      Element jqueryElement = response.createElement("script");
      jqueryElement.setAttribute("src", response.encodeURL((request.getContextPath() + "/public-resources/javascript/jquery-1.7.1.min.js")));
      jqueryElement.setAttribute("type", "text/javascript");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, jqueryElement);

      // add JavaScript
      Element jsElement = response.createElement("script");
      jsElement.setAttribute("src", response.encodeURL((request.getContextPath() + "/public-resources/javascript/openid.js")));
      jsElement.setAttribute("type", "text/javascript");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, jsElement);

      Element jsOpenIdJQueryElement = response.createElement("script");
      jsOpenIdJQueryElement.setAttribute("src", response.encodeURL((request.getContextPath() + "/public-resources/javascript/openid-jquery.js")));
      jsOpenIdJQueryElement.setAttribute("type", "text/javascript");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, jsOpenIdJQueryElement);

      Element jsOpenIdSelectorElement = response.createElement("script");
      jsOpenIdSelectorElement.setAttribute("src", response.encodeURL((request.getContextPath() + "/public-resources/javascript/openid-selector.js")));
      jsOpenIdSelectorElement.setAttribute("type", "text/javascript");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, jsOpenIdSelectorElement);
      
      chain.doFilter(request, response);
   }

   @Override
   public void destroy()
   {

   }
}
