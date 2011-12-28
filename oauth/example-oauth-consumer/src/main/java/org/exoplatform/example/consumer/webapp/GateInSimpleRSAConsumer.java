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
package org.exoplatform.example.consumer.webapp;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.ParameterStyle;
import net.oauth.example.consumer.webapp.CookieConsumer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class GateInSimpleRSAConsumer extends HttpServlet
{
   private static final long serialVersionUID = 1L;

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
   {
      OAuthConsumer consumer = null;
      try
      {
         consumer = CookieConsumer.getConsumer("gateinSimpleRSA", getServletContext());
         OAuthAccessor accessor = CookieConsumer.getAccessor(request, response, consumer);
         OAuthMessage message =
            accessor.newRequestMessage(OAuthMessage.GET,
               "http://localhost:8080/exo-oauth-provider/rest/simple", null);
         OAuthMessage result = CookieConsumer.CLIENT.invoke(message, ParameterStyle.AUTHORIZATION_HEADER);
         CookieConsumer.copyResponse(result, response);
      }
      catch (Exception e)
      {
         CookieConsumer.handleException(e, request, response, consumer);
      }
   }
}
