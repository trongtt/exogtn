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
package org.exoplatform.portal.gadget;

import java.util.LinkedList;

import junit.framework.AssertionFailedError;

import org.apache.shindig.gadgets.oauth.BasicOAuthStoreTokenIndex;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.gadget.core.GadgetToken;
import org.exoplatform.portal.gadget.core.GadgetTokenInfoService;
import org.exoplatform.web.security.AbstractTokenServiceTest;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */
@ConfiguredBy({
   @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/gadget-tokenservice-configuration.xml"),
   @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.test.jcr-configuration.xml"),
   @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/gadget-jcr-configuration.xml")
})
public class TestGadgetTokenInfoService extends AbstractTokenServiceTest<GadgetTokenInfoService>
{
   
   public TestGadgetTokenInfoService()
   {
      super();
   }
   
   protected void setUp() throws Exception
   {
      PortalContainer container = getContainer();
      service = (GadgetTokenInfoService)container.getComponentInstanceOfType(GadgetTokenInfoService.class);
      super.setUp();
      Thread.sleep(1000); // for enough time initial database
   }
   
   private LinkedList<BasicOAuthStoreTokenIndex> createTokens() throws Exception
   {
      LinkedList<BasicOAuthStoreTokenIndex> tokenIndexHolder = new LinkedList<BasicOAuthStoreTokenIndex>();
      for(int i = 0; i < 11; i++)
      {
         TokenInfo tokenInfo = new TokenInfo("accessToken" + i, "tokenSecret" + i, "sessionHandle" + i, 5 * 1000L);
         BasicOAuthStoreTokenIndex tokenIndex = new BasicOAuthStoreTokenIndex();
         tokenIndex.setGadgetUri("http://localhost:9090/gadgets" + i);
         tokenIndex.setServiceName("gadgets" + i);
         tokenIndex.setTokenName("gadgetToken" + i);
         tokenIndex.setUserId("root" + i);
         service.createToken(tokenIndex, tokenInfo);
         tokenIndexHolder.add(tokenIndex);
      }
      return tokenIndexHolder;
   }
   
   private LinkedList<GadgetToken> clearAllTokens() throws Exception
   {
      LinkedList<GadgetToken> holder = new LinkedList<GadgetToken>();
      for(BasicOAuthStoreTokenIndex tokenIndex : service.getAllTokens())
      {
         holder.add(service.deleteToken(tokenIndex));
      }
      return holder;
   }

   @Override
   public void testGetToken() throws Exception
   {
      LinkedList<BasicOAuthStoreTokenIndex> tokenIndexHolder = createTokens();
      for(int i = 0; i < tokenIndexHolder.size(); i++)
      {
         GadgetToken token = service.getToken(tokenIndexHolder.get(i));
         assertEquals("accessToken" + i, token.getAccessToken());
         assertEquals("tokenSecret" + i, token.getTokenSecret());
         assertEquals("sessionHandle" + i, token.getSessionHandle());
      }
      clearAllTokens();
   }

   @Override
   public void testGetAllToken() throws Exception
   {
      LinkedList<BasicOAuthStoreTokenIndex> tokenIndexHolder = createTokens();
      for(int i = 0; i < tokenIndexHolder.size(); i++)
      {
         BasicOAuthStoreTokenIndex tokenIndex = service.getAllTokens()[i];
         assertEquals(tokenIndex.getGadgetUri(), "http://localhost:9090/gadgets" + i);
         assertEquals(tokenIndex.getServiceName(), "gadgets" + i);
         assertEquals(tokenIndex.getTokenName(), "gadgetToken" + i);
         assertEquals(tokenIndex.getUserId(), "root" + i);
      }
      clearAllTokens();
   }

   @Override
   public void testSize() throws Exception
   {
      createTokens();
      assertEquals(11, service.size());
      clearAllTokens();
   }

   @Override
   public void testDeleteToken() throws Exception
   {
      BasicOAuthStoreTokenIndex tokenIndex = createTokens().get(0);
      GadgetToken token = service.deleteToken(tokenIndex);
      assertEquals(token.getAccessToken(), "accessToken0");
      assertEquals(token.getSessionHandle(), "sessionHandle0");
      assertEquals(token.getTokenSecret(), "tokenSecret0");
      clearAllTokens();
   }

   @Override
   public void testExpire() throws Exception
   {
      createTokens();
      Thread.sleep(3000); // for token expire
      for(BasicOAuthStoreTokenIndex tokenIndex : service.getAllTokens())
      {
         GadgetToken token = service.getToken(tokenIndex);
         assertTrue(token.isExpired());
      }
      clearAllTokens();
   }

   @Override
   public void testCleanExpireToken() throws Exception
   {
      LinkedList<BasicOAuthStoreTokenIndex> tokenIndecies = createTokens();
      Thread.sleep(5000); // for clear token expired
      assertTrue(service.size() == 0);
      for(BasicOAuthStoreTokenIndex tokenIndex : tokenIndecies)
      {
         assertNull(service.getToken(tokenIndex));
      }
   }
}
