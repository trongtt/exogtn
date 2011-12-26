package org.exoplatform.oauth.provider;

import org.exoplatform.component.test.AbstractKernelTest;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.oauth.provider.token.AccessToken;

import java.util.Map;

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
      service.registerConsumer("foo_key", "foo_secret", "http://foo.com/callbackURL", null);
      service.registerConsumer("foo_key1", "foo_secret1", "http://foo1.com/callbackURL", null);
      
      Map<String, Consumer> consumers = service.getAllConsumers();      
      Consumer c = consumers.get("foo_key");
      assertEquals("foo_key", c.getConsumerKey());
      assertEquals("foo_secret", c.getConsumerSecret());
      assertEquals("http://foo.com/callbackURL", c.getCallbackURL());
      
      Consumer c1 = consumers.get("foo_key1");
      assertEquals("foo_key1", c1.getConsumerKey());
      assertEquals("foo_secret1", c1.getConsumerSecret());
      assertEquals("http://foo1.com/callbackURL", c1.getCallbackURL());

      service.removeConsumer("foo_key");
      service.removeConsumer("foo_key1");
      assertNull(service.getConsumer("foo_key"));
      assertNull(service.getConsumer("foo_key1"));
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
      AccessToken accToken = service.generateAccessToken(reqToken);
      assertNull(service.getRequestToken(reqToken.getToken()));
      assertEquals(consumer.getConsumerKey(), accToken.getConsumerKey());
      assertEquals(reqToken.getUserId(), accToken.getUserID());
      assertNotNull(accToken.getAccessTokenID());
      assertNotNull(accToken.getAccessTokenSecret());
      
      AccessToken accToken1 = service.generateAccessToken(reqToken);
      assertEquals(accToken.getAccessTokenID(), accToken1.getAccessTokenID());
      assertEquals(accToken.getAccessTokenSecret(), accToken1.getAccessTokenSecret());
      assertEquals(accToken.getUserID(), accToken1.getUserID());
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
      AccessToken accToken = service.generateAccessToken(reqToken);
      AccessToken accToken1 = service.getAccessToken(accToken.getAccessTokenID());
      assertEquals(accToken.getAccessTokenID(), accToken1.getAccessTokenID());
      assertEquals(accToken.getAccessTokenSecret(), accToken1.getAccessTokenSecret());
      assertEquals(accToken.getUserID(), accToken1.getUserID());
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
      
      AccessToken accToken = service.generateAccessToken("root", consumer.getConsumerKey());
      assertNotNull(accToken);
      assertNotNull(accToken.getAccessTokenID());
      String token = accToken.getAccessTokenID();
      service.revokeAccessToken(token);
      assertNull(service.getAccessToken(token));
      assertNull(service.getAccessToken("root", consumer.getConsumerKey()));
      
      service.removeConsumer("foo_key");
      assertNull(service.getConsumer("foo_key"));
   }
}
