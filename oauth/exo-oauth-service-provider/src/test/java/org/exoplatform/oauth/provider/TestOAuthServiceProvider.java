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
      service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);

      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("http://foo.com/callbackURL", consumer.getCallbackURL());
      assertEquals("foo_key", consumer.getConsumerKey());
      assertEquals("foo_secret", consumer.getConsumerSecret());

      service.removeConsumer("foo_key");
      consumer = service.getConsumer("foo_key");
      assertNull(consumer);
   }
   
   public void testGetAllConsumers()
   {
     /* service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      service.registerConsumer("foo_key1", "foo_secret1", "http://foo1.com/callbackURL", null);
      
      List<Consumer> consumers = service.getAllConsumers();
      Consumer c = consumers.get(0);
      assertEquals("foo_key", c.getConsumerKey());
      assertEquals("foo_secret", c.getConsumerSecret());
      assertEquals("http://foo.com/callbackURL", c.getCallbackURL());
      
      Consumer c1 = consumers.get(1);
      assertEquals("foo_key1", c1.getConsumerKey());
      assertEquals("foo_secret1", c1.getConsumerSecret());
      assertEquals("http://foo1.com/callbackURL", c1.getCallbackURL());

      service.removeConsumer("foo_key");
      service.removeConsumer("foo_key1");
      assertNull(service.getConsumer("foo_key"));
      assertNull(service.getConsumer("foo_key1"));*/
   }
   
   public void testGenerateToken()
   {
      service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("foo_key", consumer.getConsumerKey());
      
      RequestToken reqToken = service.generateRequestToken(consumer.getConsumerKey());
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
      service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      Consumer consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("foo_key", consumer.getConsumerKey());
      
      RequestToken reqToken = service.generateRequestToken(consumer.getConsumerKey());
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
      service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
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
      service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
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
}
