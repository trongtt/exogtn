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

package org.gatein.portal.samples.api;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.StateAwareResponse;
import javax.xml.namespace.QName;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This portlet shows how to leverage the Event Protocol
 *
 * @author <a href="mailto:phuong.vu@exoplatform.com">Vu Viet Phuong</a>
 */
public class EventProtocolPortlet extends GenericPortlet
{
   private static final String SEND_REDIRECT = "sendRedirect";
   private static final String  CURRENT_STATE = "currentState";
   
   private static final String MODE = "mode";
   private static final String NORMAL_MODE = "0";
   private static final String EP_MODE = "1";
   
   private static final QName BEFORE_RENDER = new QName("http://www.gatein.org/xml/ns/ep", "BeforeRender");
   private static final QName CHANGE_NAV = new QName("http://www.gatein.org/xml/ns/ep", "ChangeNavigation");   
   
   @Override
   protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      PortletURL actURL = resp.createActionURL();
      actURL.setParameter(ActionRequest.ACTION_NAME, "ChangeState");
      
      resp.setContentType("text/html");            
      PrintWriter writer = resp.getWriter();

      String currentState = req.getParameter(CURRENT_STATE);
      String mode = req.getParameter(MODE);
      
      writer.print("<p>Current render parameter: " + currentState + "</p>");
      writer.print("<form action='" + actURL.toString() + "' method='POST'>");
      writer.print("New State: <input type='text' name='" + CURRENT_STATE  + "'/>");
      writer.print("<select name='" + MODE + "'>");
      writer.print("<option value='" + NORMAL_MODE + "'>Normal</option>");
      writer.print("<option value='" + EP_MODE + "' " + (EP_MODE.equals(mode) ? "selected" : "") + ">Use Event Protocol</option>");
      writer.print("</select>");
      writer.print("<input type='submit' value='Change state'/>");
      writer.print("</form>");

      //
      writer.close();
   }

   @Override
   public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
   {
      System.out.println("Processing action : " + request.getParameter(ActionRequest.ACTION_NAME));
      handleState(request, response);
      if (EP_MODE.equals(request.getParameter(MODE)))
      {
         response.setRenderParameter(SEND_REDIRECT, "true");         
      }
   }

   @Override
   public void processEvent(EventRequest request, EventResponse response) throws PortletException, IOException
   {
      System.out.println("Processing event : " + request.getEvent().getName());
      if (BEFORE_RENDER.equals(request.getEvent().getQName()))
      {
         if (Boolean.parseBoolean(request.getParameter(SEND_REDIRECT)))
         {
            response.setEvent(CHANGE_NAV, "test");
         }
      }      
      handleState(request, response);
   }  
   
   private void handleState(PortletRequest request, StateAwareResponse response)
   {      
      if (request.getParameter(CURRENT_STATE) != null)
      {
         response.setRenderParameter(CURRENT_STATE, request.getParameter(CURRENT_STATE));      
         response.setRenderParameter(MODE, request.getParameter(MODE));               
      }
   }
}
