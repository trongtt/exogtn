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
package org.gatein.openid.app.controller;

import org.exoplatform.openid.OpenIdUtil;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.openid.app.Session;
import org.gatein.wci.security.Credentials;
import org.juzu.Action;
import org.juzu.Path;
import org.juzu.Response;
import org.juzu.View;
import org.juzu.impl.application.InternalApplicationContext;
import org.juzu.request.ActionContext;
import org.juzu.request.RequestContext;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Dec 12, 2011
 */
public class Application
{
   private Log _log = ExoLogger.getLogger("OpenId Login Process");

   @Inject
   @Path("index.gtmpl")
   org.gatein.openid.app.templates.index index;

   @Inject
   @Path("register.gtmpl")
   org.gatein.openid.app.templates.register register;

   @Inject
   Session session;

   @View
   public void index() throws IOException
   {
      String returnUrl = Application_.processReturnURL().toString();
      index.returnUrl(returnUrl).render();
   }

   @View
   public void register() throws IOException
   {
      register.render();
   }

   @Action
   public Response login(String openid_identifier, String returnUrl)
   {
      try
      {
         ConsumerManager manager = OpenIdUtil.getConsumerManager();
         List<DiscoveryInformation> discoveries = manager.discover(openid_identifier);
         DiscoveryInformation discovery = manager.associate(discoveries);
         session.setDiscoveryInfo(discovery);
         session.setReturnUrl(returnUrl);
         returnUrl = "http://localhost:8080" + returnUrl;

         //         returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + returnUrl;

         AuthRequest authReq = manager.authenticate(discovery, returnUrl);
         FetchRequest fetch = FetchRequest.createFetchRequest();
         fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
         fetch.addAttribute("firstName", "http://schema.openid.net/namePerson/first", true);
         fetch.addAttribute("lastName", "http://schema.openid.net/namePerson/last", true);
         authReq.addExtension(fetch);

         SRegRequest sRegRequest = SRegRequest.createFetchRequest();
         sRegRequest.addAttribute("fullname", true);
         sRegRequest.addAttribute("email", true);
         authReq.addExtension(sRegRequest);

         RequestContext request = InternalApplicationContext.getCurrentRequest();
         if (request instanceof ActionContext)
         {
            return ((ActionContext) request).redirect(authReq.getDestinationUrl(true));
         }

      }
      catch (Exception e)
      {
         _log.error(e.getMessage());
      }
      return null;
   }

   @Action
   public Response processReturn() throws Exception
   {
      RequestContext request = InternalApplicationContext.getCurrentRequest();
      String token = _readOpenIdResponse(request);
      if (token == null)
      {
         //         request.setAttribute(OpenIdKeys.OPEN_ID_RESPONSE_STATUS, "error");
      }
      else
      {
         session.setOpenIdToken(token);
//         _processOpenIDAccount(request, response);
      }
      return Application_.register();
   }

   private String _readOpenIdResponse(RequestContext request)
   {
      try
      {
         ConsumerManager manager = OpenIdUtil.getConsumerManager();

         Map<String, String[]> parameterMap = request.getParameters();
         ParameterList paramList = new ParameterList(parameterMap);

         DiscoveryInformation discovered = session.getDiscoveryInfo();

         String[] returnValues = parameterMap.get("openid.return_to");
         String receivingURL = returnValues == null ? null : returnValues[0];

         VerificationResult verification = manager.verify(receivingURL, paramList, discovered);

         Identifier verified = verification.getVerifiedId();
         if (verified != null)
         {
            AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();

            if (authSuccess.hasExtension(SRegMessage.OPENID_NS_SREG))
            {
               MessageExtension ext = authSuccess.getExtension(SRegMessage.OPENID_NS_SREG);
               if (ext instanceof SRegResponse)
               {
                  SRegResponse sregResp = (SRegResponse) ext;
                  for (Iterator iter = sregResp.getAttributeNames().iterator(); iter.hasNext();)
                  {
                     String name = (String) iter.next();
                     String value = sregResp.getParameterValue(name);
                     //                     request.setAttribute(name, value);
                  }
               }
            }

            _log.info("Your OpenID: " + verified.getIdentifier());
            TransientTokenService tokenService = AbstractTokenService.getInstance(TransientTokenService.class);
            Credentials credentials = new Credentials(verified.getIdentifier(), authSuccess.getSignature());
            return tokenService.createToken(credentials);
         }
      }
      catch (OpenIDException e)
      {
         _log.error("Failure to verify Authentication Response, re-login to avoid security problem");
      }

      return null;
   }

   /*private void _processOpenIDAccount(RequestContext request)
   {
      String token = session.getOpenIdToken();
      TransientTokenService tokenService = AbstractTokenService.getInstance(TransientTokenService.class);
      Credentials tCredentials = tokenService.validateToken(token, false);
      if (tCredentials != null)
      {
         try
         {
            String identifier = tCredentials.getUsername();
            OpenIDService service = OpenIdUtil.getOpenIDService();
            String username = service.findUsernameByOpenID(identifier);
            session.setOpenIdIdentifier(identifier);
//            request.getPortletSession().setAttribute(OpenIdKeys.OPENID_IDENTIFIER, identifier);
            if (username != null)
            {
//               OpenIdUtil.autoLogin(username, request, response);
            }
            else
            {
               //ask user create account
               _log.info("Go to register new account");
//               request.setAttribute(OpenIdKeys.OPENID_IDENTIFIER, identifier);
//               request.setAttribute(OpenIdKeys.OPEN_ID_RESPONSE_STATUS, OpenIdKeys.OPEN_ID_RESPONSE_STATUS_REGISTER);
            }
         }
         catch (Exception e)
         {
            _log.error("authentication unsuccessful");
            e.printStackTrace();
         }
      }
   }*/
}
