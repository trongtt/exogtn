/**
 * Copyright (C) 2010 eXo Platform SAS.
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


import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * This filter is used to authorize a request that follows 3-legged OAuth (consumer-server-user)
 * A resource can use OAuth protection by adding this filter to their filter chain
 * 
 * See OAuth 1.0a specification for more detail
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class ExoOAuth3LeggedFilter extends ExoOAuthFilter
{
   private static final String OAUTH_AUTH_METHOD = "OAuth";

   @Override
   protected HttpServletRequest createSecurityContext(HttpServletRequest request, AccessToken accessToken)
   {
      String userId = accessToken.getUserId();
      if (userId == null)
      {
         return request;
      }
      final Principal principal = new GateInPrincipal(userId);
      final Collection<String> roles = getRoles(userId);

      return new HttpServletRequestWrapper(request)
      {
         @Override
         public Principal getUserPrincipal()
         {
            return principal;
         }

         @Override
         public boolean isUserInRole(String role)
         {
            return roles.contains(role);
         }

         @Override
         public String getAuthType()
         {
            return OAUTH_AUTH_METHOD;
         }
      };
   }

   private static class GateInPrincipal implements Principal
   {
      private String name;

      public GateInPrincipal(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return this.name;
      }
   }

   private Collection<String> getRoles(String userId)
   {
      List<String> roles = new ArrayList<String>();
      ExoContainer container = getContainer();
      OrganizationService orgService = (OrganizationService) container
            .getComponentInstanceOfType(OrganizationService.class);
      MembershipHandler membershipHandler = orgService.getMembershipHandler();
      RequestLifeCycle.begin((ComponentRequestLifecycle) orgService);
      try
      {
         Collection<Membership> collection = membershipHandler.findMembershipsByUser(userId);
         for (Membership membership : collection)
         {
            String groupId = membership.getGroupId();
            roles.add(groupId);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         RequestLifeCycle.end();
      }
      return roles;
   }

   public void destroy()
   {
   }
}
