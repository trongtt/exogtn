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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.SessionContainer;
import org.exoplatform.container.SessionManagerContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationRequestPhaseLifecycle;
import org.exoplatform.web.application.Phase;
import org.exoplatform.web.application.RequestFailure;
import org.exoplatform.webui.application.WebuiRequestContext;

public class PortalApplicationLifecycle implements ApplicationRequestPhaseLifecycle<WebuiRequestContext>
{      
   
   protected static Log log = ExoLogger.getLogger(PortalApplicationLifecycle.class);
   
   @SuppressWarnings("unused")
   public void onInit(Application app)
   {
   }

   @SuppressWarnings("unused")
   public void onStartRequest(Application app, WebuiRequestContext rcontext) throws Exception
   {
      ExoContainer pcontainer = ExoContainerContext.getCurrentContainer();
      SessionContainer.setInstance(((SessionManagerContainer)pcontainer).getSessionManager().getSessionContainer(
         rcontext.getSessionId()));
   }
   
   @SuppressWarnings("unused")
   public void onFailRequest(Application app, WebuiRequestContext rontext, RequestFailure failureType) throws Exception
   {
      
   }

   @SuppressWarnings("unused")
   public void onEndRequest(Application app, WebuiRequestContext rcontext) throws Exception
   {
      SessionContainer.setInstance(null);
      ExoContainerContext.setCurrentContainer(null);
   }

   @Override
   public void onStartRequestPhase(Application app, WebuiRequestContext context, Phase phase)
   {      
   }   

   @Override
   public void onEndRequestPhase(Application app, WebuiRequestContext context, Phase phase)
   {
      if (!Phase.ACTION.equals(phase))
      {
         return;
      }

      EventProtocolUtils.dispatchBeforeRenderEvent(context);     
   }
   
   @SuppressWarnings("unused")
   public void onDestroy(Application app)
   {
   }

}
