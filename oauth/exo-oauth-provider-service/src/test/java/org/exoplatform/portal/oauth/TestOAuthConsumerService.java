package org.exoplatform.portal.oauth;

import net.oauth.OAuthProblemException;

import net.oauth.OAuthConsumer;

import net.oauth.example.provider.core.OAuthConsumerService;

import org.exoplatform.component.test.AbstractKernelTest;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;

/**
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
@ConfiguredBy(@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/test-configuration.xml"))
public class TestOAuthConsumerService extends AbstractKernelTest
{
   private OAuthConsumerService service;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      this.service = (OAuthConsumerService)getContainer().getComponentInstanceOfType(OAuthConsumerService.class);
   }

   public void testAddingConsumer()
   {
      OAuthConsumer consumer = new OAuthConsumer("http://foo.com/callbackURL", "foo_key", "foo_secret", null);
      consumer.setProperty("description", "foo description");
      service.addConsumer("foo", consumer);

      consumer = service.getConsumer("foo");
      assertNotNull(consumer);
      assertEquals("http://foo.com/callbackURL", consumer.callbackURL);
      assertEquals("foo_key", consumer.consumerKey);
      assertEquals("foo_secret", consumer.consumerSecret);
      assertEquals("foo description", consumer.getProperty("description"));

      service.removeConsumer("foo");
      consumer = service.getConsumer("foo");
      assertNull(consumer);
   }
}
