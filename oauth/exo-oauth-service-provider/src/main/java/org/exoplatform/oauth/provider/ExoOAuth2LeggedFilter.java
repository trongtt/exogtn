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
package org.exoplatform.oauth.provider;

import javax.servlet.http.HttpServletRequest;
import org.exoplatform.oauth.provider.token.AccessToken;

/**
 * A Filter used to authorize a request that follows OAuth two legged flows
 * Apps can use OAuth protection by adding this filter to their filter chain
 * The Filter will validate some information: 
 * - oauth_consumer_key string know as consumer key
 * - oauth_signature_method algorithm used to sign, support three algorithms: PLAINTEXT, HMAC_SYMMETRIC (HMAC_SHA1), RSA_PRIVATE (RSA-SHA1)
 * - oauth_timestamp time stamp to avoid 
 * - oauth_nonce salt string create by service
 * - oauth_version version of OAuth specification (1.0, 2.0)
 * - oauth_signature signature that signed this request
 * 
 * See OAuth 1.0a specification for more detail
 * 
 * Created by The eXo Platform SAS
 * Author : Nguyen Anh Kien
 *          nguyenanhkien2a@gmail.com
 * Dec 2, 2010  
 */
public class ExoOAuth2LeggedFilter extends ExoOAuthFilter
{
   @Override
   protected HttpServletRequest createSecurityContext(HttpServletRequest request, AccessToken accessToken)
   {
      return request;
   }

   public void destroy()
   {

   }
}
