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
package org.exoplatform.oauth.authentication;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.web.filter.Filter;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.wci.security.Credentials;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A plugin of ExtensibleFilter service.
 *
 * It is visualized as a mandatory replacement of legacy OAuthLoginServlet class as
 * we aim to deploy OAuth as GateIn extension
 *
 *TODO: Provide detailed documentation
 *
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 12/16/11
 */
public class AuthenticationFilter implements Filter
{
   private static final String OAUTH_TOKEN = "oauth_token";

   private static final String OAUTH_LOGIN_TOKEN = "oauth_login_token";

   private static final String CALLBACK = "callbackURL";
   
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      process((HttpServletRequest)request, (HttpServletResponse)response);
   }

   private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      TransientTokenService tkService =
         (TransientTokenService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
            TransientTokenService.class);

      if (request.getRemoteUser() != null)
      {
         String callbackURL = request.getParameter(CALLBACK);
         String oauthToken = request.getParameter(OAUTH_TOKEN);

         if (callbackURL == null)
         {
            //TODO we should support configurable authorize URI of OAuth provider
            callbackURL =
               request.getScheme() + "://" + request.getServerName()
                  + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "")
                  + "/exo-oauth-provider/authorize";
         }

         //Create transient token to send consumer it
         String loginToken = tkService.createToken(new Credentials(request.getRemoteUser(), oauthToken));
         callbackURL += "?" + OAUTH_LOGIN_TOKEN + "=" + loginToken + "&" + OAUTH_TOKEN + "=" + oauthToken;
         response.sendRedirect(response.encodeRedirectURL(callbackURL));
      }
      else
      {
         response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      }
   }
}
