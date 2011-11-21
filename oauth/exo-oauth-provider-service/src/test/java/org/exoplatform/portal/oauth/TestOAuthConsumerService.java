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
      service.addConsumer("foo", consumer);
      
      try
      {
         consumer = service.getConsumer("foo");
         assertNotNull(consumer);
         assertEquals("http://foo.com/callbackURL", consumer.callbackURL);
         assertEquals("foo_key", consumer.consumerKey);
         assertEquals("foo_secret", consumer.consumerSecret);
      }
      catch (OAuthProblemException e)
      {
         fail("the foo consumer was not created");
      }
   }
}
