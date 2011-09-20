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
package org.exoplatform.web.controller.url;

import org.exoplatform.component.test.AbstractKernelTest;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.url.MimeType;
import org.exoplatform.web.url.URLContext;
import org.exoplatform.web.url.URLFactory;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.simple.SimpleURL;
import org.exoplatform.web.url.simple.SimpleURLContext;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 9/19/11
 */
@ConfiguredBy({
   @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/controller-configuration.xml")
})
public class TestSimpleURL extends AbstractKernelTest
{

   //Hack like this instead of giving surefire configuration in pom.xml
   static
   {
      System.setProperty("gatein.portal.controller.config", ClassLoader.getSystemClassLoader().getResource("conf/controller.xml").getPath());
   }

   private PortalContainer portalContainer;

   private WebAppController webAppController;

   private URLFactory urlFactory;

   private volatile boolean notStartYet = true;

   public void setUp()
   {
      if(notStartYet)
      {
         notStartYet = false;

         portalContainer = getContainer();
         webAppController = (WebAppController)portalContainer.getComponentInstanceOfType(WebAppController.class);
         urlFactory = (URLFactory)portalContainer.getComponentInstanceOfType(URLFactory.class);
      }
   }

   public void testPortalSite()
   {
      URLContext simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      SimpleURL simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setSchemeUse(true);
      simpleURL.setAuthorityUse(true);
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      assertEquals("http://localhost:8080/portal/classic/home", simpleURL.toString());
   }

   public void testGroupSite()
   {
      URLContext simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      SimpleURL simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setSchemeUse(true);
      simpleURL.setAuthorityUse(true);
      simpleURL.setResource(new NavigationResource(SiteType.GROUP, "/platform/administrators", "administration/registry"));
      assertEquals("http://localhost:8080/portal/g/:platform:administrators/administration/registry", simpleURL.toString());
   }

   public void testUserSite()
   {
      URLContext simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      SimpleURL simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setSchemeUse(true);
      simpleURL.setAuthorityUse(true);
      simpleURL.setResource(new NavigationResource(SiteType.USER, "root", "firstTab"));
      assertEquals("http://localhost:8080/portal/u/root/firstTab", simpleURL.toString());
   }

   public void testWithoutSchemeAndAuthority()
   {
      URLContext simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      SimpleURL simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      assertEquals("/portal/classic/home", simpleURL.toString());
   }

   public void testWithAjax()
   {
      URLContext simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      SimpleURL simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setAjax(true);
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      assertEquals("javascript:ajaxGet('/portal/classic/home?ajaxRequest=true')", simpleURL.toString());
   }

   public void testWithConfirm()
   {
      URLContext simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      SimpleURL simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setConfirm("Click to confirm");
      simpleURL.setAjax(true);
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      assertEquals("javascript:if(confirm('Click to confirm'))ajaxGet('/portal/classic/home?ajaxRequest=true')", simpleURL.toString());

      simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setConfirm("Click to confirm");
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      assertEquals("javascript:if(confirm('Click to confirm'))window.location='/portal/classic/home'", simpleURL.toString());
   }

   public void testWithQueryParam()
   {
      URLContext simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      SimpleURL simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      simpleURL.setQueryParameterValue("foo", "FOO");
      simpleURL.setQueryParameterValue("bar", "BAR");
      assertEquals("/portal/classic/home?foo=FOO&amp;bar=BAR", simpleURL.toString());

      simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setAjax(true);
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      simpleURL.setQueryParameterValue("foo", "FOO");
      simpleURL.setQueryParameterValue("bar", "BAR");
      assertEquals("javascript:ajaxGet('/portal/classic/home?foo=FOO&amp;bar=BAR&amp;ajaxRequest=true')", simpleURL.toString());
   }

   public void testXMLEscape()
   {
      URLContext simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      SimpleURL simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setMimeType(MimeType.XHTML);
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      simpleURL.setQueryParameterValue("foo", "FOO");
      simpleURL.setQueryParameterValue("bar", "BAR");
      assertEquals("/portal/classic/home?foo=FOO&amp;bar=BAR", simpleURL.toString());

      simpleURLContext = new SimpleURLContext(portalContainer, webAppController);
      simpleURL = urlFactory.newURL(SimpleURL.TYPE, simpleURLContext);
      simpleURL.setMimeType(MimeType.PLAIN);
      simpleURL.setResource(new NavigationResource(SiteType.PORTAL, "classic", "home"));
      simpleURL.setQueryParameterValue("foo", "FOO");
      simpleURL.setQueryParameterValue("bar", "BAR");
      assertEquals("/portal/classic/home?foo=FOO&bar=BAR", simpleURL.toString());
   }
}
