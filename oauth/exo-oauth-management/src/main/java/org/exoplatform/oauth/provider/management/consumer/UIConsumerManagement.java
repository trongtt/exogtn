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
 * You should have received org.exoplatform.oauth.provider.management copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.oauth.provider.management.consumer;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.oauth.provider.Consumer;
import org.exoplatform.oauth.provider.OAuthException;
import org.exoplatform.oauth.provider.OAuthServiceProvider;
import org.exoplatform.oauth.provider.OAuthToken;
import org.juzu.Action;
import org.juzu.Controller;
import org.juzu.Path;
import org.juzu.Resource;
import org.juzu.Response;
import org.juzu.plugin.ajax.Ajax;
import org.juzu.View;
import org.juzu.impl.request.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

public class UIConsumerManagement extends Controller
{
   @Inject
   @Path("index.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.index index;

   @Inject
   @Path("list.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.list list;

   @Inject
   @Path("detail.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.detail detail;

   @Inject
   @Path("add.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.add add;

   @Inject
   @Path("consumerInputs.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.consumerInputs consumerInputs;

   @Inject
   @Path("consumerFields.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.consumerFields consumerFields;
   
   @Inject
   @Path("tokenFields.gtmpl")
   org.exoplatform.oauth.provider.management.consumer.templates.tokenFields tokenFields;
   
   @Inject
   Session session;
   
   @Inject
   PortletPreferences preferences;

   @View
   public void index()
   {
      String[] pros =  preferences.getValue("extProperties", "name|description|website").split("[|]");
      session.setPropertyNames(Arrays.asList(pros));
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthServiceProvider oauthProvider =
         (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);
      index.with().render();
   }

   @View
   public void consumerDetail()
   {
      detail.with().render();
   }

   @View
   public void addConsumer()
   {
      add.with().render();
   }

   @Ajax
   @Resource
   public void search(String value, String type)
   {
      list.with().consumers(this.find(value, type)).render();
   }

   private List<Consumer> find(String value, String type)
   {
      Pattern pattern = Pattern.compile(".*");
      if (value != null && value.trim().length() > 0)
      {
         pattern = Pattern.compile(".*" + Pattern.quote(value.trim()) + ".*", Pattern.CASE_INSENSITIVE);
      }

      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthServiceProvider provider =
         (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);
      List<Consumer> results = new ArrayList<Consumer>();
      for (Consumer c : provider.getAllConsumers())
      {
         String info = "";
         if (type.equalsIgnoreCase(Constants.CONSUMER_KEY))
         {
            info = c.getConsumerKey();
         }
         else if (type.equalsIgnoreCase(Constants.CONSUMER_SECRET))
         {
            info = c.getConsumerSecret();
         }
         else if (type.equalsIgnoreCase(Constants.CONSUMER_CALLBACK_URL))
         {
            info = c.getCallbackURL();
         }
         else if (session.getPropertyNames().contains(type))
         {
            info = c.getProperty(type).toString();
         }
         else
         {
            info = c.getConsumerKey();
         }

         if (pattern.matcher(info).matches())
         {
            results.add(c);
         }
      }

      return results;
   }

   @Action
   public Response showConsumerDetail(String consumerKey)
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthServiceProvider oauthProvider =
         (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);
      Consumer consumer = oauthProvider.getConsumer(consumerKey);
      session.setConsumer(consumer);
      session.setAccessToken(oauthProvider.getAccessToken(Request.getCurrent().getBridge().getSecurityContext().getRemoteUser(), consumerKey));
      return UIConsumerManagement_.consumerDetail();
   }

   @Action
   public Response showAddConsumer()
   {
      session.setConsumer(null);
      session.setMessage(null);
      return UIConsumerManagement_.addConsumer();
   }

   @Action
   public Response deleteConsumer(String consumerKey)
   {
      if (consumerKey != null)
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         provider.removeConsumer(consumerKey);
      }
      return UIConsumerManagement_.index();
   }

   @Action
   public Response addNewConsumer()
   {
      Map<String, String> params = convert(actionContext.getParameters());
      if (!parseParameters(params))//Missing required parameters
      {
         return UIConsumerManagement_.addConsumer();
      }
      else
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         Consumer consumer = provider.getConsumer(params.get("key"));
         if (consumer != null)//Consumer is existing
         {
            StringBuilder message = new StringBuilder();
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("key", "Consumer Key is existing");
            session.setErrors(errors);
            return UIConsumerManagement_.addConsumer();
         }
         else
         {
            try
            {
               consumer =
                  provider.registerConsumer(params.remove("key"), params.remove("secret"),
                     params.remove("callback_url"), params);
               session.setConsumer(consumer);
            }
            catch (OAuthException e)
            {
               //Should log this
               e.printStackTrace();
            }
            return UIConsumerManagement_.consumerDetail();
         }
      }
   }

   @Ajax
   @Resource
   public void submitConsumerAction()
   {
      Map<String, String> params = convert(resourceContext.getParameters());
      if (!parseParameters(params))//Missing required parameters
      {
         consumerInputs.with().render();
      }
      else
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         Consumer consumer = provider.update(params.remove("key"), 
                                    params.remove("secret"), 
                                    params.remove("callback_url"), 
                                    params);
         if (consumer != null)
         {
            session.setConsumer(consumer);
         }
         consumerFields.with().render();
      }
   }

   @Ajax
   @Resource
   public void editAction(String key)
   {
      OAuthServiceProvider provider =
         (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
            OAuthServiceProvider.class);
      Consumer consumer = provider.getConsumer(key);
      Map<String, String> params = new HashMap<String, String>();
      params.put("key", consumer.getConsumerKey());
      params.put("secret", consumer.getConsumerSecret());
      params.put("callback_url", consumer.getCallbackURL());

      for (Entry<String, String> entry : consumer.getProperties().entrySet())
      {
         params.put(entry.getKey(), entry.getValue());
      }

      session.setParameters(params);
      consumerInputs.render();
   }

   @Ajax
   @Resource
   public void refreshTokenAction(String key)
   {
      OAuthServiceProvider provider =
               (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
                  OAuthServiceProvider.class);
      OAuthToken token =
         provider.generateAccessToken(Request.getCurrent().getBridge().getSecurityContext()
            .getRemoteUser(), key);
      session.setAccessToken(token);
      tokenFields.with().render();
   }
   
   private boolean parseParameters(Map<String, String> params)
   {
      Map<String, String> errors = new HashMap<String, String>();
      String key = params.get("key");
      String secret = params.get("secret");
      String callback_url = params.get("callback_url");

      if (key == null || key.length() <= 0)
      {
         errors.put("key", "Consumer key is required");
      }

      if (secret == null || secret.length() <= 0)
      {
         errors.put("secret", "Consumer secret is required");
      }

      if (callback_url == null || callback_url.length() <= 0)
      {
         errors.put("callback_url", "Callback Url is required");
      }

      if (errors.size() > 0)
      {
         session.setErrors(errors);
         session.setParameters(params);
         return false;
      }
      else
      {
         session.setParameters(params);
      }

      return true;
   }

   //Temporary during juzu support transmission Map<String, String> params
   private Map<String, String> convert(Map<String, String[]> params)
   {
      Map<String, String> results = new HashMap<String, String>();
      for (String key : params.keySet())
      {
         results.put(key, params.get(key)[0]);
      }
      return results;
   }
}
