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
package org.exoplatform.oauth.portlet;

import net.oauth.example.provider.core.SimpleOAuthServiceProvider;

import net.oauth.example.provider.core.ConsumerInfo;

import net.oauth.OAuthAccessor;

import net.oauth.example.provider.core.OAuthTokenService;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

public class UITokenManagementPortlet extends GenericPortlet
{
   @Override
   protected void doHeaders(RenderRequest request, RenderResponse response)
   {
      super.doHeaders(request, response);
      Element cssElement = response.createElement("link");
      cssElement.setAttribute("href", response.encodeURL(request.getContextPath() + "/skin/DefaultStylesheet.css"));
      cssElement.setAttribute("rel", "stylesheet");
      cssElement.setAttribute("type", "text/css");

      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, cssElement);
   }

   @RenderMode(name = "view")
   public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthTokenService provider =
         (OAuthTokenService)container.getComponentInstanceOfType(OAuthTokenService.class);

      List<OAuthAccessor> accessors = provider.getAuthorizedTokens();
      List<ConsumerInfo> consumers = new ArrayList<ConsumerInfo>();
      ConsumerInfo consumer = null;
      for (OAuthAccessor a : accessors)
      {
         if (request.getRemoteUser().equals(a.getProperty("oauth_user_id")))
         {
            consumer = SimpleOAuthServiceProvider.toConsumerInfo(a.consumer);
            consumer.setProperty("accessToken", a.accessToken);
            consumers.add(consumer);
         }
      }

      request.setAttribute("consumers", consumers);
      getPortletContext().getRequestDispatcher("/jsp/token.jsp").include(request, response);
   }

   @ProcessAction(name = "revokeAccess")
   public void revokeAccess(ActionRequest request, ActionResponse response)
   {
      String token = request.getParameter("oauth_token");
      if (token != null)
      {
         OAuthTokenService provider =
            (OAuthTokenService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthTokenService.class);
         provider.revokeAccessToken(token);
      }
   }
}
