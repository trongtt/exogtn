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

package org.exoplatform.portal.webui.navigation;

import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

import java.util.ArrayList;
import java.util.List;

public class UIPortalNavigation2 extends UIComponent
{
   private boolean useAJAX = true;

   protected PageNode selectedNode_;

   protected Object selectedParent_;

   private String template;

   @Override
   public String getTemplate()
   {
      return template != null ? template : super.getTemplate();
   }

   public void setTemplate(String template)
   {
      this.template = template;
   }

   public UIComponent getViewModeUIComponent()
   {
      return null;
   }

   public void setUseAjax(boolean bl)
   {
      useAJAX = bl;
   }

   public boolean isUseAjax()
   {
      return useAJAX;
   }

   public List<PageNavigation> getNavigations() throws Exception
   {
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
      List<PageNavigation> result = new ArrayList<PageNavigation>();
      for (PageNavigation nav : Util.getUIPortal().getNavigations())
      {
         result.add(PageNavigationUtils.filter(nav, context.getRemoteUser()));
      }
      return result;
   }

   public PageNavigation getSelectedNavigation() throws Exception
   {
      PageNavigation nav = Util.getUIPortal().getSelectedNavigation();
      if (nav != null)
         return nav;
      if (Util.getUIPortal().getNavigations().size() < 1)
         return null;
      return Util.getUIPortal().getNavigations().get(0);
   }

   public Object getSelectedParent()
   {
      return selectedParent_;
   }

   public PageNode getSelectedPageNode() throws Exception
   {
      if (selectedNode_ != null)
         return selectedNode_;
      selectedNode_ = Util.getUIPortal().getSelectedNode();
      return selectedNode_;
   }

   public boolean isSelectedNode(PageNode node)
   {
      if (selectedNode_ != null && node.getUri().equals(selectedNode_.getUri()))
         return true;
      if (selectedParent_ == null || selectedParent_ instanceof PageNavigation)
         return false;
      PageNode pageNode = (PageNode)selectedParent_;
      return node.getUri().equals(pageNode.getUri());
   }

   public void processRender(WebuiRequestContext context) throws Exception
   {
      UIPortal uiPortal = Util.getUIPortal();
      if ((uiPortal.getSelectedNode() != null) && (uiPortal.getSelectedNode() != selectedNode_))
      {
         setSelectedPageNode(uiPortal.getSelectedNode());
      }
      super.processRender(context);
   }

   private void setSelectedPageNode(PageNode selectedNode) throws Exception
   {
      selectedNode_ = selectedNode;
      selectedParent_ = null;
      String seletctUri = selectedNode.getUri();
      int index = seletctUri.lastIndexOf("/");
      String parentUri = null;
      if (index > 0)
         parentUri = seletctUri.substring(0, seletctUri.lastIndexOf("/"));
      List<PageNavigation> pageNavs = getNavigations();
      for (PageNavigation pageNav : pageNavs)
      {
         if (PageNavigationUtils.searchPageNodeByUri(pageNav, selectedNode.getUri()) != null)
         {
            if (parentUri == null || parentUri.length() < 1)
               selectedParent_ = pageNav;
            else
               selectedParent_ = PageNavigationUtils.searchPageNodeByUri(pageNav, parentUri);
            break;
         }
      }
   }

   static public class SelectNodeActionListener extends EventListener<UIPortalNavigation2>
   {
      public void execute(Event<UIPortalNavigation2> event) throws Exception
      {
         UIPortalNavigation2 uiNavigation = event.getSource();
         UIPortal uiPortal = Util.getUIPortal();
         String uri = event.getRequestContext().getRequestParameter(OBJECTID);
         int index = uri.lastIndexOf("::");
         String id = uri.substring(index + 2);
         PageNavigation selectNav = null;
         if (index <= 0)
         {
            selectNav = uiPortal.getSelectedNavigation();
         }
         else
         {
            String navId = uri.substring(0, index);
            
            //TODO: Minh Hoang TO
            //selectNav = uiPortal.getPageNavigation(Integer.parseInt(navId));
            selectNav = uiPortal.getSelectedNavigation();
         }
         PageNode selectNode = PageNavigationUtils.searchPageNodeByUri(selectNav, id);
         uiNavigation.selectedNode_ = selectNode;
         String parentUri = null;
         index = uri.lastIndexOf("/");
         if (index > 0)
            parentUri = uri.substring(0, index);
         if (parentUri == null || parentUri.length() < 1)
            uiNavigation.selectedParent_ = selectNav;
         else
            uiNavigation.selectedParent_ = PageNavigationUtils.searchPageNodeByUri(selectNav, parentUri);

         PageNodeEvent<UIPortal> pnevent;
         pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, uri);
         uiPortal.broadcast(pnevent, Event.Phase.PROCESS);
      }
   }

}
