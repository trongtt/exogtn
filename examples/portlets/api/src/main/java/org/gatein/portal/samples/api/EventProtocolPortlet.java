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
   
   public static final String PEP_NS = "http://www.gatein.org/xml/ns/pep";
   public static final QName PRE_RENDER_EVENT = new QName(PEP_NS, "pre_render");
   public static final QName CHANGE_NAV_EVENT = new QName(PEP_NS, "change_navigation");   
   public static final QName LOGIN_EVENT = new QName(PEP_NS, "login");
   public static final QName LOGOUT_EVENT = new QName(PEP_NS, "logout");
   
   @Override
   protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {             
      resp.setContentType("text/html");            
      PrintWriter writer = resp.getWriter();

      String currentState = req.getParameter(CURRENT_STATE);
      String mode = req.getParameter(MODE);
      
      PortletURL changeStateURL = resp.createActionURL();
      changeStateURL.setParameter(ActionRequest.ACTION_NAME, "ChangeState");      
      
      writer.print("<h3 style='text-align:center'>Current render parameter: " + currentState + "</h3>");
      writer.print("<form action='" + changeStateURL.toString() + "' method='POST'>");
      writer.print("&nbsp;&nbsp;New State: <input type='text' name='" + CURRENT_STATE  + "'/>");
      writer.print("<select name='" + MODE + "'>");
      writer.print("<option value='" + NORMAL_MODE + "'>Normal</option>");
      writer.print("<option value='" + EP_MODE + "' " + (EP_MODE.equals(mode) ? "selected" : "") + ">Redirect after POST</option>");
      writer.print("</select>");
      writer.print("<input type='submit' value='Change state'/>");
      writer.print("</form></br>");      
      
      writer.print("<p style='text-align:center; font-style:italic; font-weight: bold'>Change the state and then press F5 button to see the different between 2 mode </br>");
      writer.print("At Redirect mode: Portlet's listening to PRE_RENDER event and dispatch CHANGE_NAVIGATION event to refresh the page");
      writer.print("</p>");
      
      writer.print("<hr/>");
      if (req.getRemoteUser() != null)
      {
         PortletURL logoutURL = resp.createActionURL();
         logoutURL.setParameter(ActionRequest.ACTION_NAME, "Logout");
         
         writer.print("<h3 style='text-align:center'>Current user: " + req.getRemoteUser() + "</h3>");
         writer.print("<h4 style='text-align:center'><a href='" + logoutURL.toString() + "'>Logout</a></h4>");
      } 
      else 
      {
         PortletURL loginURL = resp.createActionURL();
         loginURL.setParameter(ActionRequest.ACTION_NAME, "Login");
         
         writer.print("<div style='overflow: hidden'>");
         writer.print("<h3 style='text-align:center'>Login Form</h3>");
         writer.print("<form action='" + loginURL.toString() + "' method='POST'>");
         writer.print("<table style='width: 400px; left: 200px; position: relative'>");
         writer.print("<tr><td>User Name: </td> <td><input type='text' name='username'/></td></tr>");
         writer.print("<tr><td>Password: </td><td><input type='password' name='password'/>");
         writer.print("<input type='submit' value='Login'/></td></tr></table></form></div>");         
      }
      
      //
      writer.close();
   }

   @Override
   public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
   {
      String action = request.getParameter(ActionRequest.ACTION_NAME);      
      System.out.println("Processing action : " + action);
      
      if (EP_MODE.equals(request.getParameter(MODE)) && "ChangeState".equals(action))
      {
         response.setRenderParameter(SEND_REDIRECT, "true");         
      } 
      else if ("Login".equals(action))
      {
         StringBuilder payload = new StringBuilder("{\"username\":\"");
         payload.append(request.getParameter("username"));
         payload.append("\",\"password\":\"");
         payload.append(request.getParameter("password") + "\"}");
         response.setEvent(LOGIN_EVENT, payload.toString());
      }
      else if ("Logout".equals(action))
      {
         response.setEvent(LOGOUT_EVENT, null);
      }
      
      handleState(request, response);
   }

   @Override
   public void processEvent(EventRequest request, EventResponse response) throws PortletException, IOException
   {
      System.out.println("Processing event : " + request.getEvent().getName());
      if (PRE_RENDER_EVENT.equals(request.getEvent().getQName()))
      {
         if (Boolean.parseBoolean(request.getParameter(SEND_REDIRECT)))
         {
            response.setEvent(CHANGE_NAV_EVENT, null);
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
