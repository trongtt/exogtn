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
package org.exoplatform.portal.application;

import javax.portlet.Event;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.localization.BaseHttpRequestWrapper;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.webui.application.EventsWrapper;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIPortletActionListener;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.event.Event.Phase;
import org.gatein.common.util.ParameterValidation;
import org.json.JSONObject;

/**
 * @author <a href="mailto:phuong.vu@exoplatform.com">Vu Viet Phuong</a>
 * @version $Id$
 *
 */
public class EventProtocolUtils
{
   public static final String EVENT_PROTOCOL_NS = "http://www.gatein.org/xml/ns/ep";
   
   //Portal to portlet event
   public static final QName BEFORE_RENDER_EVENT = new QName(EVENT_PROTOCOL_NS, "BeforeRender");
   
   //Portlet to portal event
   public static final QName CHANGE_NAVIGATION_EVENT = new QName(EVENT_PROTOCOL_NS, "ChangeNavigation");
   public static final String SITE_TYPE = "site_type";
   public static final String SITE_NAME = "site_name";
   public static final String NAVIGATION_URI = "navigation_uri";
   
   public static final QName LOGOUT_EVENT = new QName(EVENT_PROTOCOL_NS, "Logout");
   public static final QName LOGIN_EVENT = new QName(EVENT_PROTOCOL_NS, "Login");
   public static final String USERNAME = "username";
   public static final String PASSWORD = "password";
   public static final String INITIAL_URI = "initialURI";
   
   protected static Log log = ExoLogger.getLogger(EventProtocolUtils.class);
   
   @SuppressWarnings("rawtypes")
   public static void dispatchBeforeRenderEvent(WebuiRequestContext context)
   {
      if (context.getUIApplication() == null)
      {
         return;
      }
      
      UIPortlet uiPortlet = context.getUIApplication().findFirstComponentOfType(UIPortlet.class);
      if (uiPortlet == null)
      {
         log.warn("No portlet to dispatch BEFORE_RENDER event");
         return;
      }

      class PortletEvent implements javax.portlet.Event
      {
         QName qName;

         Serializable value;

         public PortletEvent(QName qName, Serializable value)
         {
            this.qName = qName;
            this.value = value;
         }

         public String getName()
         {
            return qName.getLocalPart();
         }

         public QName getQName()
         {
            return qName;
         }

         public Serializable getValue()
         {
            return value;
         }
      }
      
      javax.portlet.Event portletEvent = new PortletEvent(BEFORE_RENDER_EVENT, null);
      List<javax.portlet.Event> events = new LinkedList<javax.portlet.Event>();
      events.add(portletEvent);

      context.setAttribute(UIPortletActionListener.PORTLET_EVENTS, new EventsWrapper(events));
      try
      {
         uiPortlet.createEvent("ProcessEvents", org.exoplatform.webui.event.Event.Phase.PROCESS, context).broadcast();
      }
      catch (Exception e)
      {
         log.error("Problem while creating event for the portlet: " + uiPortlet.getState(), e);
      }
   }

   public static void handlePortletEvents(javax.portlet.Event event) throws Exception
   {
      QName qname = event.getQName();
      if (CHANGE_NAVIGATION_EVENT.equals(qname))
      {        
         handleChangeNavigationEvent(event);
      } 
      else if (LOGOUT_EVENT.equals(qname))
      {
         handleLogoutEvent(event);
      }
      else if (LOGIN_EVENT.equals(qname))
      {
         handleLoginEvent(event);
      } 
      else
      {
         return;
      }
      
      if (log.isDebugEnabled())
      {
         log.debug("Process event protocol: " + qname);            
      }
   }   

   private static void handleChangeNavigationEvent(javax.portlet.Event event) throws Exception
   {
      PortalRequestContext prcontext = Util.getPortalRequestContext();            
      if (prcontext == null)
      {
         log.warn("{} event can only run in portal context", CHANGE_NAVIGATION_EVENT);
         return;
      }
      
      String siteType = prcontext.getSiteType().getName();
      String siteName = prcontext.getSiteName();
      String nodePath = prcontext.getNodePath();

      Object payload = event.getValue();
      if (payload == null)
      {
         payload = "{}";
      }            
      JSONObject jsonPayload = new JSONObject(String.valueOf(payload));      
      if (!ParameterValidation.isNullOrEmpty(jsonPayload.optString(SITE_TYPE)))
      {            
         siteType = jsonPayload.optString(SITE_TYPE);
      }         
      if (!ParameterValidation.isNullOrEmpty(jsonPayload.optString(SITE_NAME)))
      {
         siteName = jsonPayload.optString(SITE_NAME);
      }
      if (!ParameterValidation.isNullOrEmpty(jsonPayload.optString(NAVIGATION_URI)))
      {
         nodePath = jsonPayload.optString(NAVIGATION_URI);
      }
      
      NavigationResource navRes = new NavigationResource(new SiteKey(siteType, siteName), nodePath);
      prcontext.sendRedirect(prcontext.createURL(NodeURL.TYPE, navRes).toString());
   }
   
   private static void handleLoginEvent(Event event) throws Exception
   {
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();            
      if (context == null)
      {
         log.warn("{} event can only run in a webui context", LOGIN_EVENT);
         return;
      }
      
      Object payload = event.getValue();
      if (payload == null)
      {
         payload = "{}";
      }      
      
      JSONObject jsonPayload = new JSONObject(String.valueOf(payload));
      final String username = jsonPayload.optString(USERNAME);
      final String password = jsonPayload.optString(PASSWORD);
      final String initialURI = jsonPayload.optString(INITIAL_URI);
       
      ConversationState state = ConversationState.getCurrent();
      if (state != null && state.getIdentity().getUserId().equals(username))
      {
         log.warn("User : {} has already login", username);
         return;
      }             
      
      HttpServletRequest req = context.getRequest();     
      HttpServletResponse res = context.getResponse();
      
      //Workaround: I can't use mergedContext.getRequestDispatcher to get servlet for /login path
      HttpServletRequest wrapper = new BaseHttpRequestWrapper(req)
      {
         @Override
         public String getParameter(String name)
         {
            if (USERNAME.equals(name)) 
            {
               return username;
            }
            if (PASSWORD.equals(name))
            {
               return password;
            }
            if (INITIAL_URI.equals(name))
            {
               return initialURI != null ? initialURI : getRequestURI(); 
            }
            return super.getParameter(name);
         }
         
      };           
      
      PortalContainer portalContainer = PortalContainer.getInstance();
      ServletContext mergedContext = portalContainer.getPortalContext();      
      mergedContext.getNamedDispatcher("PortalLoginController").forward(wrapper, res);
      
      context.setResponseComplete(true);
   }

   @SuppressWarnings("rawtypes")
   private static void handleLogoutEvent(Event event) throws Exception
   {
      UIPortal uiPortal = Util.getUIPortal();
      org.exoplatform.webui.event.Event logoutEvent = uiPortal.createEvent("Logout", 
         Phase.PROCESS, WebuiRequestContext.<WebuiRequestContext>getCurrentInstance());
      logoutEvent.broadcast();
   }
}
