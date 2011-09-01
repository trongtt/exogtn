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
package org.exoplatform.web.url.simple;

import org.exoplatform.web.controller.QualifiedName;
import org.exoplatform.web.controller.router.Router;
import org.exoplatform.web.controller.router.URIWriter;
import org.exoplatform.web.url.PortalURL;
import org.exoplatform.web.url.URLContext;
import org.gatein.common.io.UndeclaredIOException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 9/1/11
 */
public class SimpleURLContext implements URLContext
{

   //Use URIWriter to take care XML escape in returned URL
   private URIWriter writer;

   private StringBuilder buffer;

   private final String scheme;

   private final String host;

   private final int port;

   private final String contextName;

   private final Router router;

   public SimpleURLContext(String scheme, String host, int port, String contextName, Router router)
   {
      if(router == null)
      {
         throw new IllegalArgumentException("Router or any of {siteType, siteName, handler} could not be null");
      }
      this.router = router;
      this.scheme = scheme;
      this.host = host;
      this.port = port;
      this.contextName = contextName;
      this.buffer = new StringBuilder();
      this.writer = new URIWriter(buffer);
   }

   public SimpleURLContext(HttpServletRequest req, Router router)
   {
      this(req.getScheme(), req.getRemoteHost(), req.getServerPort(), req.getContextPath(), router);
   }

   public <R, U extends PortalURL<R, U>> String render(U url)
   {
      try
      {
         return _render(url);
      }
      catch(IOException ioEx)
      {
         throw new UndeclaredIOException(ioEx);
      }
   }

   private <R, U extends PortalURL<R, U>> String _render(U url) throws IOException
   {
      if(url.getResource() == null)
      {
         throw new IllegalArgumentException("There is no resource on the SimpleURL");
      }

      if(url.getSchemeUse())
      {
         buffer.append(scheme).append("://");
      }

      if(url.getAuthorityUse())
      {
         buffer.append(host);
         if(port != 80)
         {
            buffer.append(":").append(port);
         }
      }

      String confirm = url.getConfirm();
      boolean hasConfirm = confirm != null && confirm.length() > 0;
      boolean ajax = url.getAjax() != null && url.getAjax();
      if (ajax)
      {
         writer.append("javascript:");
         if (hasConfirm)
         {
            writer.append("if(confirm('");
            writer.append(confirm.replaceAll("'", "\\\\'"));
            writer.append("'))");
         }
         writer.append("ajaxGet('");
      }
      else
      {
         if (hasConfirm)
         {
            writer.append("javascript:");
            writer.append("if(confirm('");
            writer.append(confirm.replaceAll("'", "\\\\'"));
            writer.append("'))");
            writer.append("window.location=\'");
         }
      }

      Map<QualifiedName, String> parameters = new HashMap<QualifiedName, String>();
      //Inject params from resource of url to the parameters map, that might override the 4 preceding 'put' statement
      for(QualifiedName param : url.getParameterNames())
      {
         String paramValue = url.getParameterValue(param);
         if(paramValue != null)
         {
            parameters.put(param, paramValue);
         }
      }

      writer.append("/");
      writer.appendSegment(contextName);
      writer.setMimeType(url.getMimeType());
      //Rearrange parameters map might make URL rendering faster
      router.render(parameters, writer);

      //Append http request params at the end
      Map<String, String[]> queryParameters = url.getQueryParameters();
      if (queryParameters != null)
      {
         for (Map.Entry<String, String[]> entry : queryParameters.entrySet())
         {
            for (String value : entry.getValue())
            {
               writer.appendQueryParameter(entry.getKey(), value);
            }
         }
      }

      if (ajax)
      {
         writer.appendQueryParameter("ajaxRequest", "true");
         writer.append("')");
      }
      else
      {
         if (hasConfirm)
         {
            writer.append("\'");
         }
      }

      return buffer.toString();
   }
}
