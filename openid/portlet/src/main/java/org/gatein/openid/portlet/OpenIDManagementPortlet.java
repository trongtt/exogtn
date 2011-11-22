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

import org.exoplatform.openid.OpenIDService;
import org.exoplatform.openid.OpenIdUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Jul 5, 2011
 */
public class OpenIDManagementPortlet extends GenericPortlet
{
   private OpenIDService openIdService;

   public void init() throws PortletException
   {
      this.openIdService = OpenIdUtil.getOpenIDService();
      super.init();
   }

   @RenderMode(name = "VIEW")
   public void showList(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      List<String> openIds = openIdService.getAllOpenIds();
      Map<String, String> mapOpenIdEntry = new HashMap<String, String>();
      for (String openId : openIds)
      {
         String username = openIdService.findUsernameByOpenID(openId);
         mapOpenIdEntry.put(openId, username);
      }

      request.setAttribute("openids", mapOpenIdEntry);
      getPortletContext().getRequestDispatcher("/jsp/view.jsp").forward(request, response);
   }

   @ProcessAction(name = "removeOpenIdAction")
   public void removeOpenId(ActionRequest request, ActionResponse response)
   {
      String openId = request.getParameter("openId");
      openIdService.removeOpenID(openId);
   }
   
   @ProcessAction(name = "test")
   public void test(ActionRequest request, ActionResponse response) throws Exception
   {
      System.out.println("hahaha");
      response.sendRedirect("http://google.com");
   }
}
