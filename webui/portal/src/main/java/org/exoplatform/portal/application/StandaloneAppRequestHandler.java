/**
 * Copyright (C) 2009 eXo Platform SAS.
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

package org.exoplatform.portal.application;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.web.ControllerContext;
import org.exoplatform.web.WebAppController;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class StandaloneAppRequestHandler extends PortalRequestHandler
{
   
   private String webuiConfigPath;

   public StandaloneAppRequestHandler(InitParams params)
   {
      ValueParam valueParam = params.getValueParam("webui.configuration");
      if (valueParam != null)
      {
         webuiConfigPath = valueParam.getValue();
      }
   }

   @Override
   public String getHandlerName()
   {
      return "standalone";
   }

   @Override
   public void onInit(WebAppController controller, ServletConfig sConfig) throws Exception
   {
      application = new StandaloneApplication(sConfig);
      ((StandaloneApplication)application).setWebUIConfigPath(webuiConfigPath);
      application.onInit();
   }
   
   @Override
   public boolean execute(ControllerContext controllerContext, HttpServletRequest request, HttpServletResponse response) throws Exception
   {
      log.debug("Session ID = " + request.getSession().getId());
      response.setHeader("Cache-Control", "no-cache");

      //
      String requestPath = controllerContext.getParameter(REQUEST_PATH);

      StandaloneAppRequestContext context = new StandaloneAppRequestContext(application, controllerContext, request, response, requestPath);
      processRequest(context, application);
      return true;
   }
}
