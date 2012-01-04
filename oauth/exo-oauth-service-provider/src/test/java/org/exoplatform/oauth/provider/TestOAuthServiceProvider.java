package org.exoplatform.oauth.provider;

import org.exoplatform.component.test.AbstractKernelTest;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;

import java.util.List;

/**
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
@ConfiguredBy({
   @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.test.jcr-configuration.xml"),
   @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.identity-configuration.xml"),
   @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.portal-configuration.xml"),
   @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.oauth.test.provider-configuration.xml")})
public class TestOAuthServiceProvider extends AbstractKernelTest
{
   private OAuthServiceProvider service;

   @Override
   protected void setUp() throws Exception
   {
      begin();
      this.service = (OAuthServiceProvider)getContainer().getComponentInstanceOfType(OAuthServiceProvider.class);
   }
   
   @Override
   protected void tearDown() throws Exception
   {
      end();
   }

   public void testRegisterConsumer()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      }
      catch (OAuthException e)
      {
         fail(e.getMessage());
      }
      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("http://foo.com/callbackURL", consumer.getCallbackURL());
      assertEquals("foo_key", consumer.getConsumerKey());
      assertEquals("foo_secret", consumer.getConsumerSecret());

      service.removeConsumer("foo_key");
      assertNull(service.getConsumer("foo_key"));
   }
   
   public void testRegisterDuplicationConsumer()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
         Consumer consumer = service.getConsumer("foo_key");
         assertNotNull(consumer);
         assertEquals("foo_key", consumer.getConsumerKey());
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
         fail("Cannot register new consumer");
      }
      catch (OAuthException e)
      {
         assertTrue(e.getMessage(), e.getMessage().contains(OAuthKeys.OAUTH_DUPLICATE_CONSUMER));
      }
      finally
      {
         service.removeConsumer("foo_key");
         assertNull(service.getConsumer("foo_key"));
      }
   }
   
   public void testRegisterManyConsumers()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
         service.registerConsumer("foo_key1", "foo_secret1", "http://foo1.com/callbackURL", null);
      }
      catch (OAuthException e)
      {
         fail(e.getMessage());
      }
      Consumer c = service.getConsumer("foo_key");
      assertEquals("foo_key", c.getConsumerKey());
      assertEquals("foo_secret", c.getConsumerSecret());
      assertEquals("http://foo.com/callbackURL", c.getCallbackURL());
      
      Consumer c1 = service.getConsumer("foo_key1");
      assertEquals("foo_key1", c1.getConsumerKey());
      assertEquals("foo_secret1", c1.getConsumerSecret());
      assertEquals("http://foo1.com/callbackURL", c1.getCallbackURL());

      service.removeConsumer("foo_key");
      service.removeConsumer("foo_key1");
      assertNull(service.getConsumer("foo_key"));
      assertNull(service.getConsumer("foo_key1"));
   }
   
   public void testRemoveConsumer()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      }
      catch (OAuthException e)
      {
         fail(e.getMessage());
      }
      Consumer c = service.getConsumer("foo_key");
      assertEquals("foo_key", c.getConsumerKey());
      assertEquals("foo_secret", c.getConsumerSecret());
      assertEquals("http://foo.com/callbackURL", c.getCallbackURL());
      
      //Remove consumer that is not existing
      service.removeConsumer("foo_key1");
      assertNull(service.getConsumer("foo_key1"));
      
      service.removeConsumer("foo_key");
      assertNull(service.getConsumer("foo_key"));

      //Remove again
      service.removeConsumer("foo_key");
      assertNull(service.getConsumer("foo_key"));
   }
   
   public void testGenerateToken()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      }
      catch (OAuthException e)
      {
         fail(e.getMessage());
      }
      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("foo_key", consumer.getConsumerKey());
      
      RequestToken reqToken = service.generateRequestToken(consumer.getConsumerKey());
      assertNotNull(reqToken);
      assertEquals(consumer.getConsumerKey(), reqToken.getConsumerKey());
      assertNotNull(reqToken.getToken());
      assertNotNull(reqToken.getTokenSecret());
      
      reqToken.setUserId("root");
      OAuthToken accToken = service.generateAccessToken(reqToken);
      assertNull(service.getRequestToken(reqToken.getToken()));
      assertEquals(consumer.getConsumerKey(), accToken.getConsumerKey());
      assertEquals(reqToken.getUserId(), accToken.getUserId());
      assertNotNull(accToken.getToken());
      assertNotNull(accToken.getTokenSecret());
      
      OAuthToken accToken1 = service.generateAccessToken(reqToken);
      assertEquals(accToken.getToken(), accToken1.getToken());
      assertEquals(accToken.getTokenSecret(), accToken1.getTokenSecret());
      assertEquals(accToken.getUserId(), accToken1.getUserId());
      assertEquals(accToken.getConsumerKey(), accToken1.getConsumerKey());
      
      service.removeConsumer("foo_key");
      assertNull(service.getConsumer("foo_key"));
   }
   
   public void testGetToken()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      }
      catch (OAuthException e)
      {
         fail(e.getMessage());
      }
      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("foo_key", consumer.getConsumerKey());
      
      RequestToken reqToken = service.generateRequestToken(consumer.getConsumerKey());
      assertNotNull(reqToken);
      assertNotNull(reqToken.getToken());
      assertNotNull(reqToken.getTokenSecret());
      
      RequestToken reqToken1 = service.getRequestToken(reqToken.getToken());
      assertEquals(reqToken.getToken(), reqToken1.getToken());
      assertEquals(reqToken.getTokenSecret(), reqToken1.getTokenSecret());
      assertEquals(reqToken.getConsumerKey(), reqToken1.getConsumerKey());
      
      reqToken.setUserId("root");
      OAuthToken accToken = service.generateAccessToken(reqToken);
      OAuthToken accToken1 = service.getAccessToken(accToken.getToken());
      assertEquals(accToken.getToken(), accToken1.getToken());
      assertEquals(accToken.getTokenSecret(), accToken1.getTokenSecret());
      assertEquals(accToken.getUserId(), accToken1.getUserId());
      assertEquals(accToken.getConsumerKey(), accToken1.getConsumerKey());
      
      service.removeConsumer("foo_key");
      assertNull(service.getConsumer("foo_key"));
   }
   
   public void testConsumerAndAuthorizedToken()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      }
      catch (OAuthException e)
      {
         fail(e.getMessage());
      }
      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("foo_key", consumer.getConsumerKey());
      
      service.generateAccessToken("root", consumer.getConsumerKey());
      
      service.removeConsumer("foo_key");
      assertNull(service.getConsumer("foo_key"));
      assertNull(service.getAccessToken("root", consumer.getConsumerKey()));
   }
   
   public void testRevokeToken()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      }
      catch (OAuthException e)
      {
         fail(e.getMessage());
      }
      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("foo_key", consumer.getConsumerKey());
      
      RequestToken reqToken = service.generateRequestToken(consumer.getConsumerKey());
      assertNotNull(reqToken);
      assertNotNull(reqToken.getToken());
      service.revokeRequestToken(reqToken.getToken());
      assertNull(service.getRequestToken(reqToken.getToken()));     
      
      OAuthToken accToken = service.generateAccessToken("root", consumer.getConsumerKey());
      assertNotNull(accToken);
      assertNotNull(accToken.getToken());
      service.revokeAccessToken(accToken.getToken());
      assertNull(service.getAccessToken(accToken.getToken()));
      assertNull(service.getAccessToken("root", consumer.getConsumerKey()));
      
      service.removeConsumer("foo_key");
      assertNull(service.getConsumer("foo_key"));
   }
   
   public void testGetAccessTokens()
   {
      try
      {
         service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      }
      catch (OAuthException e)
      {
         fail(e.getMessage());
      }
      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("foo_key", consumer.getConsumerKey());
      
      //Access token must belong to an User and a Consumer
      OAuthToken accessToken = service.generateAccessToken("root", consumer.getConsumerKey());
      assertNotNull(accessToken);
      assertNotNull(accessToken.getConsumerKey());
      assertNotNull(accessToken.getUserId());
      
      List<OAuthToken> tokens = service.getAccessTokens();
      assertEquals(1, tokens.size());
      OAuthToken authorizedToken = tokens.get(0);
      assertEquals(accessToken.getToken(), authorizedToken.getToken());
      assertEquals(accessToken.getTokenSecret(), authorizedToken.getTokenSecret());
      assertEquals(accessToken.getUserId(), authorizedToken.getUserId());
      assertEquals(accessToken.getConsumerKey(), authorizedToken.getConsumerKey());
      
      service.generateAccessToken("john", consumer.getConsumerKey());
      tokens = service.getAccessTokens();
      assertEquals(2, tokens.size());
      
      service.revokeAccessToken(tokens.get(0).getToken());
      service.revokeAccessToken(tokens.get(1).getToken());
      tokens = service.getAccessTokens();
      assertEquals(0, tokens.size());
   }
}
