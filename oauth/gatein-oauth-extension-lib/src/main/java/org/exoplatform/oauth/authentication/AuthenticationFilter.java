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
import org.exoplatform.web.security.GateInToken;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.wci.security.Credentials;
import java.io.IOException;
import java.net.URI;
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

   private static final String AUTH_TOKEN = "auth_token";

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
   {
      process((HttpServletRequest)request, (HttpServletResponse)response);
   }

   private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      TransientTokenService tkService = (TransientTokenService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(TransientTokenService.class);

      String authToken = request.getParameter(AUTH_TOKEN);

      //TODO: Explain this check
      if (authToken != null)
      {
         if (request.getRemoteUser() != null && tkService.getToken(authToken) != null)
         {
            GateInToken oToken = tkService.getToken(authToken);
            String callbackURL = oToken.getPayload().getUsername();
            String oauthToken = oToken.getPayload().getPassword();

            //Create another transient token to send consumer it
            String loginToken = tkService.createToken(new Credentials(request.getRemoteUser(), oauthToken));
            tkService.deleteToken(authToken);

            //TODO: Examine security leak as password is injected into redirect response
            callbackURL += "?" + OAUTH_LOGIN_TOKEN + "=" + loginToken + "&" + OAUTH_TOKEN + "=" + oToken.getPayload().getPassword();
            response.sendRedirect(response.encodeRedirectURL(callbackURL));
         }
         else
         {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
         }
      }
      else
      {
         try
         {
            String callbackURL = request.getParameter("callback");
            String token = request.getParameter("oauth_token");
            if (callbackURL == null || token == null)
            {
               throw new Exception("parameter_absent");
            }
            URI uri = new URI(callbackURL);
            if (!uri.isAbsolute())
            {
               callbackURL =
                  request.getLocalAddr() + (callbackURL.startsWith("/") ? uri.toString() : "/" + uri.toString());
            }

            //Create a transient token with Credentials: callbackURL(as username) and oauth_token (as password)
            authToken = tkService.createToken(new Credentials(callbackURL, token));
            String initialURI = request.getRequestURI() + "?" + AUTH_TOKEN + "=" + authToken;
            String redirectURI = request.getContextPath() + "/dologin?initialURI=" + initialURI;
            response.sendRedirect(response.encodeRedirectURL(redirectURI));
         }
         catch (Exception e)
         {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
         }
      }
   }
}
