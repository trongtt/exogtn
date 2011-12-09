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

import net.oauth.example.provider.core.AccessToken;
import net.oauth.example.provider.core.ConsumerInfo;
import net.oauth.example.provider.core.OAuthKeys;
import net.oauth.example.provider.core.OAuthServiceProvider;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
      OAuthServiceProvider provider =
         (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);

      Collection<AccessToken> tokens = provider.getAuthorizedTokens();
      Map<AccessToken, ConsumerInfo> accessors = new HashMap<AccessToken, ConsumerInfo>();
      for(AccessToken token : tokens)
      {
         if (request.getRemoteUser().equals(token.getUserId()))
         {
            ConsumerInfo consumer = provider.getConsumer(token.getConsumerKey());
            accessors.put(token, consumer);
         }
      }

      request.setAttribute("accessors", accessors);
      getPortletContext().getRequestDispatcher("/jsp/token.jsp").include(request, response);
   }

   @ProcessAction(name = "revokeAccess")
   public void revokeAccess(ActionRequest request, ActionResponse response)
   {
      String token = request.getParameter(OAuthKeys.OAUTH_TOKEN);
      if (token != null)
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         provider.revokeAccessToken(token);
      }
   }
}
