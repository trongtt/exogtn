package org.exoplatform.portal.oauth;

import net.oauth.example.provider.core.ConsumerInfo;
import net.oauth.example.provider.core.OAuthServiceProvider;

import org.exoplatform.component.test.AbstractKernelTest;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;

/**
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
@ConfiguredBy(@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/test-configuration.xml"))
public class TestOAuthServiceProvider extends AbstractKernelTest
{
   private OAuthServiceProvider service;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      this.service = (OAuthServiceProvider)getContainer().getComponentInstanceOfType(OAuthServiceProvider.class);
   }

   public void testAddingConsumer()
   {
      ConsumerInfo consumer = new ConsumerInfo("foo_key", "foo_secret", "http://foo.com/callbackURL");
      service.addConsumer(consumer);

      consumer = service.getConsumer("foo_key");
      assertNotNull(consumer);
      assertEquals("http://foo.com/callbackURL", consumer.getCallbackUrl());
      assertEquals("foo_key", consumer.getConsumerKey());
      assertEquals("foo_secret", consumer.getConsumerSecret());

      service.removeConsumer("foo_key");
      consumer = service.getConsumer("foo_key");
      assertNull(consumer);
   }
}
