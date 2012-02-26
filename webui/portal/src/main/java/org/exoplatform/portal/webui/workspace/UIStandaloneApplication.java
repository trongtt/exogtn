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

package org.exoplatform.portal.webui.workspace;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.application.StandaloneAppRequestContext;
import org.exoplatform.portal.controller.resource.ResourceId;
import org.exoplatform.portal.controller.resource.ResourceScope;
import org.exoplatform.portal.controller.resource.script.FetchMap;
import org.exoplatform.portal.controller.resource.script.FetchMode;
import org.exoplatform.portal.resource.Skin;
import org.exoplatform.portal.resource.SkinService;
import org.exoplatform.portal.webui.application.UIStandaloneAppContainer;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.LocaleContextInfo;
import org.exoplatform.services.resources.Orientation;
import org.exoplatform.web.application.JavascriptManager;
import org.exoplatform.web.application.javascript.JavascriptConfigService;
import org.exoplatform.web.url.MimeType;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.url.ComponentURL;

@ComponentConfig(lifecycle = UIStandaloneApplicationLifecycle.class, template = "system:/groovy/portal/webui/workspace/UIStandaloneApplication.gtmpl")
public class UIStandaloneApplication extends UIApplication
{         
   private String skin_ = "Default";

   private Orientation orientation_ = Orientation.LT;
   
   public static final UIComponent EMPTY_COMPONENT = new UIComponent(){
      public String getId() { return "{portal:componentId}"; };
   };

   public UIStandaloneApplication() throws Exception
   {
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
      LocaleConfigService localeConfigService = getApplicationComponent(LocaleConfigService.class);
      Locale locale = context.getLocale();
      if (locale == null)
      {
         if (log.isWarnEnabled())
            log.warn("No locale set on StandaloneAppRequestContext! Falling back to 'en'.");
         locale = Locale.ENGLISH;
      }

      String localeName = LocaleContextInfo.getLocaleAsString(locale);
      LocaleConfig localeConfig = localeConfigService.getLocaleConfig(localeName);
      if (localeConfig == null)
      {
         if (log.isWarnEnabled())
            log.warn("Unsupported locale set on PortalRequestContext: " + localeName + "! Falling back to 'en'.");
         localeConfig = localeConfigService.getLocaleConfig(Locale.ENGLISH.getLanguage());
      }
      setOrientation(localeConfig.getOrientation());
      
      addChild(UIStandaloneAppContainer.class, null, null);
   }  

   //Temporary need this, don't want to render UIPopupMessage
   public void renderChildren() throws Exception
   {
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
      super.renderChildren(context);
   }

   @Override
   public void processDecode(WebuiRequestContext context) throws Exception
   {
      String storageId = ((StandaloneAppRequestContext)context).getStorageId();      
      
      UIStandaloneAppContainer staContaner = getChild(UIStandaloneAppContainer.class);
      if (!storageId.equals(staContaner.getCurrStorageId()) )
      {         
         staContaner.setCurrStorageId(storageId);
      }
      super.processDecode(context);
   }

   public void processRender(WebuiRequestContext context) throws Exception
   {
      Writer w = context.getWriter();

      //
      if (!context.useAjax())
      {
         super.processRender(context);
      }
      else
      {
         PortalRequestContext pcontext = (PortalRequestContext)context;

         Set<UIComponent> list = context.getUIComponentToUpdateByAjax();

         w.write("<div class=\"PortalResponse\">");
         w.write("<div class=\"PortalResponseData\">");
         if (list != null)
         {
            for (UIComponent uicomponent : list)
            {
               if (log.isDebugEnabled())
               {
                  log.debug("AJAX call: Need to refresh the UI component " + uicomponent.getName());
               }
               renderBlockToUpdate(uicomponent, context, w);
            }
         }
         w.write("</div>");
         
         w.write("<div class=\"MarkupHeadElements\"></div>");
         w.write("<div class=\"LoadingScripts\">");
         writeLoadingScripts(pcontext);
         w.write("</div>");
         w.write("<div class=\"PortalResponseScript\">");
         JavascriptManager jsManager = pcontext.getJavascriptManager();         
         w.write(jsManager.getJavaScripts());
         w.write("</div>");
         w.write("</div>");
      }
   }

   public Map<String, Boolean> getScriptsURLs()
   {
      PortalRequestContext prc = PortalRequestContext.getCurrentInstance();
      
      // Obtain the resource ids involved
      // we clone the fetch map by safety
      JavascriptManager jsMan = prc.getJavascriptManager();
      FetchMap<ResourceId> requiredResources = new FetchMap<ResourceId>(jsMan.getScriptResources());

      // Need to add bootstrap as immediate since it contains the loader
      requiredResources.add(new ResourceId(ResourceScope.SHARED, "bootstrap"), FetchMode.IMMEDIATE);

      //
      log.debug("Resource ids to resolve: " + requiredResources);

      //
      JavascriptConfigService service = getApplicationComponent(JavascriptConfigService.class);
      
      // We need the locale
      Locale locale = prc.getLocale();

      try
      {
         LinkedHashMap<String, Boolean> ret = new LinkedHashMap<String, Boolean>();

         //
         FetchMap<String> urls = new FetchMap<String>(service.resolveURLs(
            prc.getControllerContext(),
            requiredResources,
            !PropertyManager.isDevelopping(),
            !PropertyManager.isDevelopping(),
            locale));
         urls.addAll(jsMan.getExtendedScriptURLs());
         
         //
         log.info("Resolved URLS for urls: " + urls);
         
         // Here we get the list of stuff to load on demand or not
         // according to the boolean value in the map
         // Convert the map to what the js expects to have
         for (Map.Entry<String, FetchMode> entry : urls.entrySet())
         {
            ret.put(entry.getKey(), entry.getValue() == FetchMode.ON_LOAD);
         }

         return ret;
      }
      catch (IOException e)
      {
         log.error("Could not resolve URLs", e);
         return Collections.emptyMap();
      }
   }
   
   private void writeLoadingScripts(PortalRequestContext context) throws Exception
   {
      Writer w = context.getWriter();
      Map<String, Boolean> scriptURLs = getScriptsURLs();
      List<String> onloadJS = new LinkedList<String>();
      for (String url : scriptURLs.keySet()) 
      {
         if (scriptURLs.get(url))
         {
            onloadJS.add(url);
         }
      }
      w.write("<div class=\"OnloadScripts\">");
      for (String url : onloadJS)
      {
         w.write(url);
         w.write(",");
      }
      w.write("</div>");
      w.write("<div class=\"ImmediateScripts\">");
      scriptURLs.keySet().removeAll(onloadJS);
      for (String url : scriptURLs.keySet())
      {
         w.write(url);
         w.write(",");
      }
      
      JavascriptManager jsManager = context.getJavascriptManager();
      for (String url : jsManager.getImportedJavaScripts())
      {
         w.write(url);
         w.write(",");
      }
      w.write("</div>");      
   }

   public Collection<Skin> getPortalSkins()
   {
      SkinService skinService = getApplicationComponent(SkinService.class);
      Collection<Skin> skins = new ArrayList<Skin>(skinService.getPortalSkins(skin_));
      return skins;
   }

   public String getSkin()
   {
      return skin_;
   }

   public Orientation getOrientation()
   {
      return orientation_;
   }

   public void setOrientation(Orientation orientation)
   {
      this.orientation_ = orientation;
   }

   public Locale getLocale()
   {
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
      return context != null ? context.getLocale() : null;
   }

   /**
    * Return the portal url template which will be sent to client ( browser )
    * and used for JS based portal url generation.
    *
    * <p>The portal url template are calculated base on the current request and site state.
    * Something like : <code>"/portal/g/:platform:administrators/administration/registry?portal:componentId={portal:uicomponentId}&portal:action={portal:action}" ;</code>
    *
    * @return return portal url template
    * @throws java.io.UnsupportedEncodingException
    */
   public String getPortalURLTemplate() throws UnsupportedEncodingException
   {
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      ComponentURL urlTemplate = pcontext.createURL(ComponentURL.TYPE);
      urlTemplate.setMimeType(MimeType.PLAIN);
      urlTemplate.setPath(pcontext.getNodePath());
      urlTemplate.setResource(EMPTY_COMPONENT);
      urlTemplate.setAction("{portal:action}");

      return urlTemplate.toString();
   }
}
