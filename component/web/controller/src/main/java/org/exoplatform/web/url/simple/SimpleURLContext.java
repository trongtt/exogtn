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

import org.exoplatform.commons.utils.I18N;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.controller.QualifiedName;
import org.exoplatform.web.controller.router.Router;
import org.exoplatform.web.controller.router.URIWriter;
import org.exoplatform.web.url.PortalURL;
import org.exoplatform.web.url.URLContext;
import org.gatein.common.io.UndeclaredIOException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 9/1/11
 */
public class SimpleURLContext implements URLContext
{

   private static final String HTTP_SCHEME = "http";

   private static final String DEFAULT_HOST = "localhost";

   private static final int DEFAULT_PORT = 8080;

   private static final String DEFAULT_SERVLET_CONTEXT = "portal";

   private static final String DEFAULT_REQUEST_HANDLER = "portal";

   private URIWriter writer;

   private StringBuilder buffer;

   protected String scheme;

   protected String host;

   protected int port;

   protected String servletContext;

   protected String requestHandler;

   private Router router;

   public SimpleURLContext(String scheme, String host, int port, String servletContext, String requestHandler, Router router)
   {
      if(router == null)
      {
         throw new IllegalArgumentException("Router could not be null");
      }
      this.router = router;
      this.scheme = scheme;
      this.host = host;
      this.port = port;
      this.servletContext = servletContext;
      this.requestHandler = requestHandler;
      this.buffer = new StringBuilder();
      this.writer = new URIWriter(buffer);
   }

   public SimpleURLContext(String servletContext, Router router)
   {
      this(HTTP_SCHEME, DEFAULT_HOST, DEFAULT_PORT, servletContext, DEFAULT_REQUEST_HANDLER, router);
   }

   public SimpleURLContext(HttpServletRequest httpRequest, PortalContainer container, WebAppController controller)
   {
      this(httpRequest.getScheme(), httpRequest.getRemoteHost(), httpRequest.getServerPort(), container.getName(), DEFAULT_REQUEST_HANDLER, controller.getRouter());
   }

   public SimpleURLContext(PortalContainer container, WebAppController controller)
   {
      this(container.getName(), controller.getRouter());
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

      writer.setMimeType(url.getMimeType());

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

      parameters.put(WebAppController.HANDLER_PARAM, requestHandler);

      Locale locale = url.getLocale();
      if(locale != null && locale.getLanguage().length() > 0)
      {
         parameters.put(SimpleURL.LANG, I18N.toTagIdentifier(locale));
      }
      else
      {
         parameters.put(SimpleURL.LANG, "");
      }

      for(QualifiedName param : url.getParameterNames())
      {
         String paramValue = url.getParameterValue(param);
         if(paramValue != null)
         {
            parameters.put(param, paramValue);
         }
      }

      writer.append("/");
      writer.appendSegment(servletContext);

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
