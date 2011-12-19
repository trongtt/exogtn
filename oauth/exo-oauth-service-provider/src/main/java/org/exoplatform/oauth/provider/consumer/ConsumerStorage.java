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
package org.exoplatform.oauth.provider.consumer;

import org.chromattic.api.ChromatticSession;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.oauth.provider.ConsumerInfo;
import org.exoplatform.services.organization.OrganizationService;
import org.gatein.common.io.IOTools;
import org.picocontainer.Startable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 12/14/11
 */
public class ConsumerStorage implements Startable
{

   private ChromatticLifeCycle lifecycle;

   private TaskExecutor executor;

   public ConsumerStorage(ChromatticManager manager, /* Pico is crap */OrganizationService orgService) throws Exception
   {
      lifecycle = manager.getLifeCycle("oauth");
      executor = new TaskExecutor(lifecycle);
   }

   public void start()
   {
      scheduleEviction();

      try
      {
         RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());

         //Not a good way to avoid reinitiating of data
         Collection<Consumer> consumers = getConsumers();
         if (consumers == null || consumers.size() < 1)
         {
            loadInitConfig();
         }
      }
      finally
      {
         RequestLifeCycle.end();
      }
   }

   private void scheduleEviction()
   {
      //TODO
   }

   private void loadInitConfig()
   {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/consumer.properties");
      try
      {
         Properties config = new Properties();
         config.load(in);

         Enumeration keys = config.keys();
         while(keys.hasMoreElements())
         {
            String key = keys.nextElement().toString();
            if(!key.contains("."))
            {
               String secret = config.getProperty(key);
               String desc = config.getProperty(key + ".description");
               String callbackURL = config.getProperty(key + ".callbackURL");
               Map<String, String> properties = new HashMap<String, String>();
               properties.put("name", key);
               properties.put("description", desc);

               registerConsumer(key, secret, callbackURL, properties);
            }
         }
      }
      catch(IOException ioEx)
      {
         ioEx.printStackTrace();
      }
      finally
      {
         IOTools.safeClose(in);
      }
   }

   private ConsumerContainer getConsumerContainer(ChromatticSession session)
   {
      ConsumerContainer container = session.findByPath(ConsumerContainer.class, "consumers");
      if(container == null)
      {
         synchronized(this)
         {
            if((container = session.findByPath(ConsumerContainer.class, "consumers")) == null)
            {
               container = session.insert(ConsumerContainer.class, "consumers");
               session.save();
            }
         }
      }
      return container;
   }

   public void registerConsumer(final ConsumerInfo consumerInfo)
   {
      Task<Void> registerTask = new Task<Void>()
      {
         @Override
         public Void run(ChromatticSession session)
         {
            ConsumerContainer container = getConsumerContainer(session);
            Consumer inf = container.create();
            container.getConsumers().put(consumerInfo.getConsumerKey(), inf);

            inf.setKey(consumerInfo.getConsumerKey());
            inf.setSecret(consumerInfo.getConsumerSecret());
            inf.setCallbackURL(consumerInfo.getCallbackUrl());

            for (Map.Entry<String, Object> entry : consumerInfo.getProperties().entrySet())
            {
               ConsumerProperty property = inf.createProperty();
               inf.getProperties().put(entry.getKey(), property);
               property.setPropertyValue(entry.getValue().toString());
            }
            return null;
         }
      };
      executor.execute(registerTask);
   }

   public void registerConsumer(final String key, final String secret, final String callbackURL, final Map<String, String> properties)
   {
      Task<Void> registerTask = new Task<Void>()
      {
         @Override
         public Void run(ChromatticSession session)
         {
            ConsumerContainer container = getConsumerContainer(session);
            Consumer consumerInf = container.create();
            container.getConsumers().put(key, consumerInf);
            consumerInf.setKey(key);
            consumerInf.setSecret(secret);
            consumerInf.setCallbackURL(callbackURL);

            for (Map.Entry<String, String> entry : properties.entrySet())
            {
               ConsumerProperty property = consumerInf.createProperty();
               consumerInf.getProperties().put(entry.getKey(), property);
               property.setPropertyValue(entry.getValue());
            }
            return null;
         }
      };
      executor.execute(registerTask);
   }

   public Consumer getConsumer(String key)
   {
      ChromatticSession session = lifecycle.getContext().getSession();
      return getConsumerContainer(session).getConsumer(key);
   }

   public Collection<Consumer> getConsumers()
   {
      ChromatticSession session = lifecycle.getContext().getSession();
      return Collections.unmodifiableCollection(getConsumerContainer(session).getConsumers().values());
   }

   public Consumer deleteConsumer(final String key)
   {
      Task<Consumer> deleteTask = new Task<Consumer>()
      {
         @Override
         public Consumer run(ChromatticSession session)
         {
            ConsumerContainer container = getConsumerContainer(session);
            return container.getConsumers().remove(key);
         }
      };
      return executor.execute(deleteTask);
   }

   public List<Consumer> deleteConsumers(final List<String> keys)
   {
      Task<List<Consumer>> deleteTask = new Task<List<Consumer>>()
      {
         @Override
         public List<Consumer> run(ChromatticSession session)
         {
            List<Consumer> deletedConsumers = new LinkedList<Consumer>();
            ConsumerContainer container = getConsumerContainer(session);
            for(String key : keys)
            {
               Consumer deletedConsumer = container.getConsumers().remove(key);
               if(deletedConsumer != null)
               {
                  deletedConsumers.add(deletedConsumer);
               }
            }
            return deletedConsumers;
         }
      };
      return executor.execute(deleteTask);
   }

   public void stop()
   {
   }
}