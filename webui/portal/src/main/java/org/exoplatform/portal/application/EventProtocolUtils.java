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

import javax.xml.namespace.QName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.webui.application.EventsWrapper;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIPortletActionListener;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * @author <a href="mailto:phuong.vu@exoplatform.com">Vu Viet Phuong</a>
 * @version $Id$
 *
 */
public class EventProtocolUtils
{
   public static final String EVENT_PROTOCOL_NS = "http://www.gatein.org/xml/ns/ep";
   
   //Portal to portlet event
   public static final QName BEFORE_RENDER = new QName(EVENT_PROTOCOL_NS, "BeforeRender");
   
   //Portlet to portal event
   public static final QName CHANGE_NAVIGATION = new QName(EVENT_PROTOCOL_NS, "ChangeNavigation");
   private static final String SITE_TYPE = "site_type";
   private static final String SITE_NAME = "site_name";
   private static final String NAVIGATION_URI = "navigation_uri";
   
   protected static Log log = ExoLogger.getLogger(EventProtocolUtils.class);
   
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
      
      javax.portlet.Event portletEvent = new PortletEvent(BEFORE_RENDER, null);
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
      if (CHANGE_NAVIGATION.equals(event.getQName()))
      {
         if (log.isDebugEnabled())
         {
            log.debug("Processing event protocol: " + CHANGE_NAVIGATION);            
         }
         handleChangeNavigationEvent(event);
      }
   }

   private static void handleChangeNavigationEvent(javax.portlet.Event event) throws Exception
   {
      PortalRequestContext prcontext = Util.getPortalRequestContext();            
      String siteType = prcontext.getSiteType().getName();
      String siteName = prcontext.getSiteName();
      String nodePath = prcontext.getNodePath();

      if (event.getValue() instanceof Map)
      {
         Map payLoad = (Map)event.getValue();
         if (payLoad.get(SITE_TYPE) != null)
         {            
            siteType = payLoad.get(SITE_TYPE).toString();
         }         
         if (payLoad.get(SITE_NAME) != null)
         {
            siteName = payLoad.get(SITE_NAME).toString();
         }
         if (payLoad.get(NAVIGATION_URI) != null)
         {
            nodePath = payLoad.get(NAVIGATION_URI).toString();
         }
      }
      NavigationResource navRes = new NavigationResource(new SiteKey(siteType, siteName), nodePath);
      prcontext.sendRedirect(prcontext.createURL(NodeURL.TYPE, navRes).toString());
   }
}
