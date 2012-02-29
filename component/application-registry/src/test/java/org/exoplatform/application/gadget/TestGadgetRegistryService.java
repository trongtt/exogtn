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

package org.exoplatform.application.gadget;

import org.chromattic.ext.ntdef.NTFolder;
import org.chromattic.ext.ntdef.Resource;
import org.exoplatform.application.AbstractApplicationRegistryTest;
import org.exoplatform.application.gadget.impl.GadgetDefinition;
import org.exoplatform.application.gadget.impl.GadgetRegistryServiceImpl;
import org.exoplatform.application.gadget.impl.LocalGadgetData;
import org.exoplatform.application.gadget.impl.RemoteGadgetData;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.gatein.common.io.IOTools;
import org.gatein.common.net.URLTools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

/**
 * Created by The eXo Platform SAS Author : Pham Thanh Tung
 * thanhtungty@gmail.com Jul 11, 2008
 */
public class TestGadgetRegistryService extends AbstractApplicationRegistryTest
{

   private GadgetRegistryServiceImpl service_;

   private ChromatticManager chromatticManager;

   private ConfigurationManager configurationManager;

   public void setUp() throws Exception
   {
      PortalContainer container = PortalContainer.getInstance();
      service_ = (GadgetRegistryServiceImpl) container.getComponentInstanceOfType(GadgetRegistryService.class);
      chromatticManager = (ChromatticManager) container.getComponentInstanceOfType(ChromatticManager.class);
      configurationManager = (ConfigurationManager) container.getComponentInstanceOfType(ConfigurationManager.class);
      begin();
   }

   @Override
   protected void tearDown() throws Exception
   {
      chromatticManager.getSynchronization().setSaveOnClose(false);
      end();
   }

   public void testLocalGadget() throws Exception
   {
      String gadgetName = "local_test";
      TestGadgetImporter importer = new TestGadgetImporter(configurationManager, gadgetName, "org/exoplatform/application/gadgets/weather.xml", true);
      importer.doImport();
      assertEquals(1, service_.getAllGadgets().size());
      assertEquals(gadgetName, service_.getGadget(gadgetName).getName());
      service_.removeGadget(gadgetName);
      assertNull(service_.getGadget(gadgetName));
   }
   
   public void testRemoteGadget() throws Exception
   {
      String gadgetName = "remote_test";
      TestGadgetImporter importer = new TestGadgetImporter(configurationManager, gadgetName, "http://www.labpixies.com/campaigns/weather/weather.xml", false);
      importer.doImport();
      assertEquals(1, service_.getAllGadgets().size());
      assertEquals(gadgetName, service_.getGadget(gadgetName).getName());
      service_.removeGadget(gadgetName);
      assertNull(service_.getGadget(gadgetName));
   }

   public void testSaveAndUpdateGadget() throws Exception
   {
      String gadgetName = "remote";
      Gadget gadget = new Gadget();
      gadget.setName(gadgetName);
      gadget.setUrl("http://www.labpixies.com/campaigns/weather/weather.xml");
      gadget.setLocal(false);
      gadget.setTitle("Remote Gadget");
      service_.saveGadget(gadget);
      assertEquals(1, service_.getAllGadgets().size());
      assertEquals(gadgetName, service_.getGadget(gadgetName).getName());
      assertEquals("http://www.labpixies.com/campaigns/weather/weather.xml", service_.getGadgetURL(gadgetName));
      
      gadget.setTitle("New Remote Title");
      service_.saveGadget(gadget);
      assertEquals(1, service_.getAllGadgets().size());
      assertEquals(gadgetName, service_.getGadget(gadgetName).getName());
      assertEquals("http://www.labpixies.com/campaigns/weather/weather.xml", service_.getGadgetURL(gadgetName));
      assertEquals("New Remote Title", service_.getGadget(gadgetName).getTitle());
      
      service_.removeGadget(gadgetName);
      assertNull(service_.getGadget(gadgetName));
   }
   
   public void testManyGadgets() throws Exception
   {
      String localName = "local_gadget";
      String remoteName = "remote_gadget";
      TestGadgetImporter importer1 = new TestGadgetImporter(configurationManager, remoteName, "http://www.labpixies.com/campaigns/weather/weather.xml", false);
      TestGadgetImporter importer2 = new TestGadgetImporter(configurationManager, localName, "org/exoplatform/application/gadgets/weather.xml", true);
      importer1.doImport();
      importer2.doImport();
      
      assertEquals("http://www.labpixies.com/campaigns/weather/weather.xml", service_.getGadgetURL(remoteName));      
      assertEquals(2, service_.getAllGadgets().size());
      
      List<Gadget> gadgets = service_.getAllGadgets(new Comparator<Gadget>()
      {         
         public int compare(Gadget o1, Gadget o2)
         {
            return o1.getName().compareToIgnoreCase(o2.getName());
         }
      });
      assertEquals(2, gadgets.size());
      assertEquals(localName, gadgets.get(0).getName());
      assertEquals(remoteName, gadgets.get(1).getName());
      
      service_.removeGadget(localName);
      service_.removeGadget(remoteName);
      assertEquals(0, service_.getAllGadgets().size());
   }
   
   class TestGadgetImporter extends GadgetImporter
   {
      private boolean local_;

      private ConfigurationManager configurationManager;

      protected TestGadgetImporter(ConfigurationManager configurationManager, String gadgetName, String gadgetURI,
            boolean local)
      {
         super(gadgetName, gadgetURI);
         this.local_ = local;
         this.configurationManager = configurationManager;
      }

      @Override
      protected byte[] getGadgetBytes(String gadgetURI) throws IOException
      {
         if (local_)
         {
            String filePath = "classpath:/" + gadgetURI;
            InputStream in;
            try
            {
               in = configurationManager.getInputStream(filePath);
               if (in != null)
               {
                  return IOTools.getBytes(in);
               }
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
         else if (!local_)
         {
            URL url;
            url = new URL(gadgetURI);
            return URLTools.getContent(url, 5000, 5000);
         }

         throw new IllegalArgumentException("Gadget URI is not correct");
      }

      @Override
      protected String getGadgetURL() throws Exception
      {
         return getGadgetURI();
      }

      @Override
      protected void process(String gadgetURI, GadgetDefinition def) throws Exception
      {
         def.setLocal(local_);
         if (local_)
         {
            byte[] content = getGadgetBytes(gadgetURI);
            if (content != null)
            {
               LocalGadgetData data = (LocalGadgetData) def.getData();
               data.setFileName(gadgetURI);
               NTFolder folder = data.getResources();
               String encoding = EncodingDetector.detect(new ByteArrayInputStream(content));
               folder.createFile(getName(gadgetURI), new Resource(LocalGadgetData.GADGET_MIME_TYPE, encoding, content));
            }
         }
         else
         {
            RemoteGadgetData data = (RemoteGadgetData) def.getData();
            data.setURL(gadgetURI);
         }
      }

      private String getName(String resourcePath) throws IOException
      {
         // Get index of last '/'
         int index = resourcePath.lastIndexOf('/');

         // Return name
         return resourcePath.substring(index + 1);
      }

      public void doImport() throws Exception
      {
         GadgetDefinition def = service_.getRegistry().addGadget(getGadgetName());
         doImport(def);
      }
   }
}
