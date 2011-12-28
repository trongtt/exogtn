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
package org.exoplatform.oauth.provider;

import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.server.OAuthServlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.oauth.provider.impl.SimpleOAuthServiceProvider;
import org.exoplatform.oauth.provider.validation.AccessorBuilder;
import org.exoplatform.services.security.web.SetCurrentIdentityFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Dec 8, 2011
 */
public abstract class AbstractExoOAuthFilter extends SetCurrentIdentityFilter
{
   final public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException
   {
      try
      {
         RequestLifeCycle.begin(getContainer());
         _doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
      }
      finally
      {
         RequestLifeCycle.end();
      }
   }

   protected void _doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
         throws IOException, ServletException
   {
      try
      {
         ExoContainer container = getContainer();
         OAuthServiceProvider provider = (OAuthServiceProvider) container.getComponentInstanceOfType(OAuthServiceProvider.class);
         OAuthMessage requestMessage = OAuthServlet.getMessage(request, null);

         OAuthToken token = provider.getAccessToken(requestMessage.getToken());
         if (token == null || token.getToken() == null)
         {
            throw new OAuthProblemException(OAuthKeys.OAUTH_TOKEN_EXPIRED);
         }
         else
         {
            OAuthValidator validator = (OAuthValidator)container.getComponentInstanceOfType(OAuthValidator.class);
            validator.validateMessage(requestMessage, AccessorBuilder.buildAccessor(token, provider));
            request.setAttribute(OAuthKeys.OAUTH_USER_ID, token.getUserId());
            request = createSecurityContext(request, token);
            super.doFilter(request, response, chain);
         }
      }
      catch (Exception e)
      {
         SimpleOAuthServiceProvider.handleException(e, request, response, false);
      }
   }

   protected abstract HttpServletRequest createSecurityContext(HttpServletRequest request, OAuthToken accessToken);
}
