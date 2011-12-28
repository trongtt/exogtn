/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
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
 * @author <a href="nguyenanhkien2a@gmail.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class GateInSimple2LegsConsumer extends HttpServlet
{
   private static final long serialVersionUID = 1L;

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
   {
      OAuthConsumer consumer = null;
      try
      {
         String oauth_token = request.getParameter("oauth_token");
         String oauth_token_secret = request.getParameter("oauth_token_secret");
         if(oauth_token == null || oauth_token_secret == null)
         {
            request.getRequestDispatcher("gateintoken.jsp").forward(request, response);
            return;
         }
         consumer = CookieConsumer.getConsumer("gateinSimple2Legs", getServletContext());
         OAuthAccessor accessor = CookieConsumer.getAccessor(request, response, consumer, true);
         accessor.accessToken = oauth_token;
         accessor.tokenSecret = oauth_token_secret;
         OAuthMessage message =
            accessor.newRequestMessage(OAuthMessage.GET,
               "http://localhost:8080/exo-oauth-provider/rest/2legs", null);
         OAuthMessage result = CookieConsumer.CLIENT.invoke(message, ParameterStyle.AUTHORIZATION_HEADER);

         CookieConsumer.copyResponse(result, response);
      }
      catch (Exception e)
      {
         CookieConsumer.handleException(e, request, response, consumer);
      }
   }
}
