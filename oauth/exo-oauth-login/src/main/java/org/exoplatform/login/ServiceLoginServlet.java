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
package org.exoplatform.login;

import org.exoplatform.container.web.AbstractHttpServlet;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

@SuppressWarnings("serial")
public class ServiceLoginServlet extends AbstractHttpServlet
{
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      String callbackURL = request.getParameter("callbackURL");

      try
      {
         URI uri = new URI(callbackURL);
         if (!uri.isAbsolute())
         {
            callbackURL =
               request.getLocalAddr() + (callbackURL.startsWith("/") ? uri.toString() : "/" + uri.toString());
         }
      }
      catch (Exception e)
      {
         response.sendError(HttpServletResponse.SC_BAD_REQUEST);
         return;
      }

      if (request.getRemoteUser() != null)
      {
         response.sendRedirect(response.encodeRedirectURL(callbackURL));
      }
      else
      {
         String initialURI = request.getRequestURI() + "?callbackURL=" + callbackURL;
         String redirectURI = request.getContextPath() + "/dologin?initialURI=" + initialURI;
         response.sendRedirect(response.encodeRedirectURL(redirectURI));
      }
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      doGet(request, response);
   }
}
