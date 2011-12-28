/**
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
package org.exoplatform.oauth.provider.example.rest;

import com.thoughtworks.xstream.XStream;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.oauth.provider.Consumer;
import org.exoplatform.oauth.provider.OAuthServiceProvider;
import org.exoplatform.services.rest.resource.ResourceContainer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
@Path("consumers")
public class ConsumerManagementRest implements ResourceContainer
{
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Object list(@Context HttpServletRequest request)
   {
      // Only Administrators has permission to get list of consumers
      if (request.isUserInRole("/platform/administrators"))
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         List<Consumer> consumers = provider.getAllConsumers();
         XStream xstream = new XStream();
         xstream.setMode(XStream.NO_REFERENCES);
         xstream.alias("consumer", Consumer.class);
         return xstream.toXML(consumers);
      }
      
      String error = "<error>";
      error += "Hello: " + request.getRemoteUser();
      error += ". Only Administrators has permission, you don't have permission to get list of OAuth consumers";
      error += "</error>";
      return error;
   }
}
