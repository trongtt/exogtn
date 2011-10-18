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

package org.exoplatform.portal.gadget.core;

import com.google.inject.Singleton;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import sun.misc.BASE64Encoder;

import org.apache.shindig.auth.BlobCrypterSecurityTokenCodec;
import org.apache.shindig.config.ContainerConfigException;
import org.apache.shindig.expressions.Expressions;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.commons.utils.Safe;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * <p>The goal of the container config subclass is to integrate security key files along
 * with exo configuration.</p>
 * <p>
 * The implementation first determine the most relevant locations of key files for performing the lookup.
 * Ideally it will take ones configured as properties <i>gatein.gadgets.securityTokenKeyFile</i>
 * and <i>gatein.gadgets.signingKeyFile</i> in the <i>configuration.properties</i>.
 * If these properties are not configured, then the implementation uses the current execution directory
 * (which should be /bin in tomcat and jboss).</p>
 *
 * <p>When the lookup file locations are determined, the implementation looks for these key files.
 * If no such files are found, then it will attempt to create them with a base 64 value encoded from
 * a 32 bytes random sequence generated by {@link SecureRandom} seeded by the current time. If the
 * file exist already but is a directory then no action is done.<p>
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */

@Singleton
public class
   ExoContainerConfig extends GateInJsonContainerConfig
{

   /** . */
   private Log log = ExoLogger.getLogger(ExoContainerConfig.class);

   /** . */
   private static volatile String tokenKey_;
   private String signingKey_;

   @Inject
   public ExoContainerConfig(@Named("shindig.containers.default") String s, Expressions expressions)
      throws ContainerConfigException
   {
      super(s, expressions);

      // This ensures RootContainer initialized first
      // to populate properties in configuration.properties into PropertyManager
      RootContainer.getInstance();
      
      initializeTokenKeyFile();
      initializeSigningKeyFile();
   }
   
   private void initializeTokenKeyFile()
   {
      String keyPath = PropertyManager.getProperty("gatein.gadgets.securitytokenkeyfile");
      
      File tokenKeyFile = null;      
      if (keyPath == null) 
      {
         log.warn("The gadgets token key is not configured. The default key.txt file in /bin will be used");
         tokenKeyFile = new File("key.txt");
      }
      else
      {
         tokenKeyFile = new File(keyPath);
      }
      
      keyPath = tokenKeyFile.getAbsolutePath();
      if (tokenKeyFile.exists())
      {
         if (tokenKeyFile.isFile())
         {
            setTokenKeyPath(keyPath);
            log.info("Found token key file " + keyPath + " for gadgets security");
         }
         else
         {
            log.error("Found token path file " + keyPath + " but it's not a key file");
         }
      }
      else
      {
         log.debug("No token key file found at path " + keyPath + ". it's generating a new key and saving it");
         File fic = tokenKeyFile.getAbsoluteFile();
         File parentFolder = fic.getParentFile();
         if (!parentFolder.exists()) {
            if (!parentFolder.mkdirs())
            {
               log.error("Coult not create parent folder/s for the token key file " + keyPath);
               return;
            }
         }
         String key = generateKey();
         Writer out = null;
         try
         {
            out = new FileWriter(tokenKeyFile);
            out.write(key);
            out.write('\n');
            setTokenKeyPath(keyPath);
            log.debug("Generated token key file " + keyPath + " for eXo Gadgets");
         }
         catch (IOException e)
         {
            log.error("Could not create token key file " + keyPath, e);
         }
         finally
         {
            Safe.close(out);
         }
      }
   }

   private void initializeSigningKeyFile()
   {
      String signingKey = PropertyManager.getProperty("gatein.gadgets.signingkeyfile");
      
      File signingKeyFile;
      if (signingKey == null)
      {
         log.warn("The gadgets signing key is not configured. The default signing key in /bin directory will be used.");
         signingKeyFile = new File("oauthkey.pem");
      }
      else
      {
         signingKeyFile = new File(signingKey);
      }
      
      if (signingKeyFile.exists())
      {
         if (signingKeyFile.isFile())
         {
            signingKey_ = signingKeyFile.getAbsolutePath();
            log.info("Use signing key " +  signingKey_ + " for gadget security");
         }
         else
         {
            log.error("Found signing path file " + signingKeyFile.getAbsolutePath() + " but it's not a key file");
         }
      }
   }
   
   private void setTokenKeyPath(String keyPath)
   {
      // _keyPath is volatile so no concurrent writes and read are safe
      synchronized (ExoContainerConfig.class)
      {
         if (tokenKey_ != null && !tokenKey_.equals(keyPath))
         {
            throw new IllegalStateException("There is already a configured key path old=" + tokenKey_ + " new="
               + keyPath);
         }
         tokenKey_ = keyPath;
      }
   }

   @Override
   public Object getProperty(String container, String property)
   {
      if (property.equals(BlobCrypterSecurityTokenCodec.SECURITY_TOKEN_KEY_FILE) && tokenKey_ != null)
      {
         return tokenKey_;
      }
      if (property.equals(ExoOAuthModule.SIGNING_KEY_FILE) && signingKey_ != null)
      {
         return signingKey_;
      }
      return super.getProperty(container, property);
   }

   /**
    * It's not public as we don't want to expose it to the outter world. The fact that this class
    * is instantiated by Guice and the ExoDefaultSecurityTokenGenerator is managed by exo kernel
    * force us to use static reference to share the keyPath value.
    *
    * @return the key path
    */
   static String getTokenKeyPath()
   {
      return tokenKey_;
   }

   /**
    * Generate a key of 32 bytes encoded in base64. The generation is based on
    * {@link SecureRandom} seeded with the current time.
    *
    * @return the key
    */
   private static String generateKey()
   {
      try
      {
         SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
         random.setSeed(System.currentTimeMillis());
         byte bytes[] = new byte[32];
         random.nextBytes(bytes);
         BASE64Encoder encoder = new BASE64Encoder();
         return encoder.encode(bytes);
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new AssertionError(e);
      }
   }
}
