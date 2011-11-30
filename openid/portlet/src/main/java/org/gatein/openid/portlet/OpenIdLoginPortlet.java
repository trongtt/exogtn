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
package org.gatein.openid.portlet;

import org.exoplatform.openid.OpenIDService;
import org.exoplatform.openid.OpenIdKeys;
import org.exoplatform.openid.OpenIdUtil;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.wci.security.Credentials;
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
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.ProcessAction;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletException;

/**
 * A Portlet for displaying UI such as enter openId identifier, register user or mapping
 * 
 * @author <a href="mailto:ndkhoi168@gmail.com">Nguyen Duc Khoi</a>
 * Nov 21, 2011
 */
public class OpenIdLoginPortlet extends GenericPortlet
{
   private Log _log = ExoLogger.getLogger(OpenIdLoginPortlet.class);

   @Override
   protected void doHeaders(RenderRequest request, RenderResponse response)
   {
      super.doHeaders(request, response);

      // add CSS
      Element cssElement = response.createElement("link");
      cssElement.setAttribute("href", response.encodeURL((request.getContextPath() + "/css/openid.css")));
      cssElement.setAttribute("rel", "stylesheet");
      cssElement.setAttribute("type", "text/css");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, cssElement);

      Element cssSelectorElement = response.createElement("link");
      cssSelectorElement.setAttribute("href",
            response.encodeURL((request.getContextPath() + "/css/openid-selector.css")));
      cssSelectorElement.setAttribute("rel", "stylesheet");
      cssSelectorElement.setAttribute("type", "text/css");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, cssSelectorElement);

      Element jqueryElement = response.createElement("script");
      jqueryElement.setAttribute("src", response.encodeURL((request.getContextPath() + "/js/jquery-1.7.1.min.js")));
      jqueryElement.setAttribute("type", "text/javascript");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, jqueryElement);

      // add JavaScript
      Element jsElement = response.createElement("script");
      jsElement.setAttribute("src", response.encodeURL((request.getContextPath() + "/js/openid.js")));
      jsElement.setAttribute("type", "text/javascript");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, jsElement);

      Element jsOpenIdJQueryElement = response.createElement("script");
      jsOpenIdJQueryElement
            .setAttribute("src", response.encodeURL((request.getContextPath() + "/js/openid-jquery.js")));
      jsOpenIdJQueryElement.setAttribute("type", "text/javascript");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, jsOpenIdJQueryElement);

      Element jsOpenIdSelectorElement = response.createElement("script");
      jsOpenIdSelectorElement.setAttribute("src",
            response.encodeURL((request.getContextPath() + "/js/openid-selector.js")));
      jsOpenIdSelectorElement.setAttribute("type", "text/javascript");
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, jsOpenIdSelectorElement);
   }

   @RenderMode(name = "view")
   public void view(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      PrintWriter writer = response.getWriter();
      writer.write("<div id=\"OpenIdPortlet\">");
      String jspPath = "/jsp/login.jsp";
      if (request.getRemoteUser() == null)
      {
         _generateUrls(request, response);

         String responseStatus = (String) request.getAttribute(OpenIdKeys.OPEN_ID_RESPONSE_STATUS);

         if (responseStatus != null)
         {
            if (responseStatus.equals(OpenIdKeys.OPEN_ID_RESPONSE_STATUS_REGISTER))
            {
               jspPath = "/jsp/register.jsp";
            }
            else if (responseStatus.equals(OpenIdKeys.OPEN_ID_RESPONSE_STATUS_MAP))
            {
               jspPath = "/jsp/mapuser.jsp";
            }
         }
      }
      else
      {
         String loggedUser = request.getRemoteUser();
         String message = "You are logged in as <b>" + loggedUser + "</b>";
         String identifier = (String) request.getPortletSession().getAttribute(OpenIdKeys.OPENID_IDENTIFIER);
         if (identifier != null)
         {
            message += "<br />You are using an OpenId to log in the system " + identifier;
         }

         request.setAttribute("message", message);
      }

      getPortletContext().getRequestDispatcher(jspPath).include(request, response);
      writer.write("</div>");
      writer.close();
   }

   /**
    * Process openId login
    * <p>This action is invoked when user enter an openId identifier or choose an OP to login</p>
    * @param request
    * @param response
    * @throws Exception
    */
   @ProcessAction(name = PortletURLConstant.LOGIN_ACTION)
   public void loginAction(ActionRequest request, ActionResponse response) throws Exception
   {
      this._sendOpenIdRequest(request, response);
   }

   /**
    * Process the request sent from openId provider
    * <p>This action's URL has been set as <code>return_url</code> in request to the OP, after login process at OP side successfully
    * OP send a request to the <code>return_url</code></p>
    * 
    * @param request
    * @param response
    */
   @ProcessAction(name = PortletURLConstant.PROCESS_RETURN_ACTION)
   public void processReturnAction(ActionRequest request, ActionResponse response)
   {
      String token = _readOpenIdResponse(request, response);
      if (token == null)
      {
         request.setAttribute(OpenIdKeys.OPEN_ID_RESPONSE_STATUS, "error");
      }
      else
      {
         request.setAttribute(OpenIdKeys.OPEN_ID_RESPONSE_STATUS, "OK");
         request.getPortletSession().setAttribute(OpenIdKeys.OPENID_TOKEN, token);
         _processOpenIDAccount(request, response);
      }
   }

   /**
    * Create a GateIn account for new user
    * 
    * @param request
    * @param response
    */
   @ProcessAction(name = PortletURLConstant.REGISTER_ACTION)
   public void registerAction(ActionRequest request, ActionResponse response)
   {
      _processRegisterAccount(request, response);
   }

   /**
    * Process login with username and password entered by user
    * 
    * @param request
    * @param response
    */
   @ProcessAction(name = PortletURLConstant.PROCESS_MAPPING_OPENID_ACTION)
   public void processMappingOpenIdAction(ActionRequest request, ActionResponse response)
   {
      _log.info("Process Mapping OpenId Action");
      _processMappingOpenId(request, response);
   }

   @ProcessAction(name = PortletURLConstant.PROCESS_ROUTE_ACTION)
   public void routeAction(ActionRequest request, ActionResponse response)
   {
      String form = request.getParameter("form");
      request.setAttribute(OpenIdKeys.OPEN_ID_RESPONSE_STATUS, form);
   }

   private void _processMappingOpenId(ActionRequest request, ActionResponse response)
   {
      String identifier = (String) request.getPortletSession().getAttribute(OpenIdKeys.OPENID_IDENTIFIER);

      //Submit from register.jsp
      String username = request.getParameter("username");
      String password = request.getParameter("password");
      if (username == null || password == null || identifier == null)
         return;
      try
      {
         //Verify username and password
         Authenticator authenticator = (Authenticator) OpenIdUtil.getContainer().getComponentInstanceOfType(
               Authenticator.class);

         if (authenticator == null)
         {
            _log.error("No Authenticator component found, check your configuration");
            throw new ServletException("There is an internal error of Server");
         }

         Credential[] credentials = new Credential[]
         {new UsernameCredential(username), new PasswordCredential(password)};

         String userId = authenticator.validateUser(credentials);

         //Map openID and user
         OpenIDService service = OpenIdUtil.getOpenIDService();
         service.mapToUser(identifier, userId);
         //Auto login
         OpenIdUtil.autoLogin(userId, request, response);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         _log.error("Username or Password is invalid: " + e.getMessage());

         //Go back to mapuser screen
         request.setAttribute("error", "Username or Password is invalid");
      }
      return;
   }

   private void _processRegisterAccount(ActionRequest request, ActionResponse response)
   {
      User userData = new UserImpl(request.getParameter("username"));
      userData.setPassword(request.getParameter("password"));
      userData.setEmail(request.getParameter("email"));
      userData.setFirstName(request.getParameter("firstName"));
      userData.setLastName(request.getParameter("lastName"));

      try
      {
         User user = OpenIdUtil.getOpenIDService().createUser(userData,
               request.getParameter(OpenIdKeys.OPENID_IDENTIFIER));
         if (user != null)
         {
            //Auto Login
            OpenIdUtil.autoLogin(user.getUserName(), request, response);
         }
         _log.info("Create successfully user: " + user.getUserName());
      }
      catch (Exception e)
      {
         _log.error("Cannot create new user: ");
         e.printStackTrace();
      }
   }

   private void _processOpenIDAccount(ActionRequest request, ActionResponse response)
   {
      String token = (String) request.getPortletSession().getAttribute(OpenIdKeys.OPENID_TOKEN);
      TransientTokenService tokenService = AbstractTokenService.getInstance(TransientTokenService.class);
      Credentials tCredentials = tokenService.validateToken(token, false);
      if (tCredentials != null)
      {
         try
         {
            String identifier = tCredentials.getUsername();
            OpenIDService service = OpenIdUtil.getOpenIDService();
            String username = service.findUsernameByOpenID(identifier);
            request.getPortletSession().setAttribute(OpenIdKeys.OPENID_IDENTIFIER, identifier);
            if (username != null)
            {
               OpenIdUtil.autoLogin(username, request, response);
            }
            else
            {
               //ask user create account
               _log.info("Go to register new account");
               request.setAttribute(OpenIdKeys.OPENID_IDENTIFIER, identifier);
               request.setAttribute(OpenIdKeys.OPEN_ID_RESPONSE_STATUS, OpenIdKeys.OPEN_ID_RESPONSE_STATUS_REGISTER);
            }
         }
         catch (Exception e)
         {
            _log.error("authentication unsuccessful");
            e.printStackTrace();
         }
      }
   }

   private void _generateUrls(RenderRequest request, RenderResponse response)
   {
      PortletURL loginAction = response.createActionURL();
      loginAction.setParameter(ActionRequest.ACTION_NAME, PortletURLConstant.LOGIN_ACTION);
      request.setAttribute(PortletURLConstant.LOGIN_ACTION, loginAction);

      PortletURL processReturnURL = response.createActionURL();
      processReturnURL.setParameter(ActionRequest.ACTION_NAME, PortletURLConstant.PROCESS_RETURN_ACTION);
      request.setAttribute("returnurl", processReturnURL);

      PortletURL registerActionURL = response.createActionURL();
      registerActionURL.setParameter(ActionRequest.ACTION_NAME, PortletURLConstant.REGISTER_ACTION);
      request.setAttribute(PortletURLConstant.REGISTER_ACTION, registerActionURL);

      PortletURL processMappingOpenIdURL = response.createActionURL();
      processMappingOpenIdURL.setParameter(ActionRequest.ACTION_NAME, PortletURLConstant.PROCESS_MAPPING_OPENID_ACTION);
      request.setAttribute(PortletURLConstant.PROCESS_MAPPING_OPENID_ACTION, processMappingOpenIdURL);

      PortletURL routeActionURL = response.createActionURL();
      routeActionURL.setParameter(ActionRequest.ACTION_NAME, PortletURLConstant.PROCESS_ROUTE_ACTION);
      request.setAttribute(PortletURLConstant.PROCESS_ROUTE_ACTION, routeActionURL);
   }

   private void _sendOpenIdRequest(ActionRequest request, ActionResponse response) throws Exception
   {
      try
      {
         String openid_identifier = request.getParameter("openid_identifier");

         ConsumerManager manager = OpenIdUtil.getConsumerManager();
         List<DiscoveryInformation> discoveries = manager.discover(openid_identifier);
         DiscoveryInformation discovered = manager.associate(discoveries);
         request.getPortletSession().setAttribute(OpenIdKeys.OPENID_DISCOVERY, discovered);

         String returnUrl = request.getParameter("returnurl");
         returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + returnUrl;

         AuthRequest authReq = manager.authenticate(discovered, returnUrl);
         FetchRequest fetch = FetchRequest.createFetchRequest();
         fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
         fetch.addAttribute("firstName", "http://schema.openid.net/namePerson/first", true);
         fetch.addAttribute("lastName", "http://schema.openid.net/namePerson/last", true);
         authReq.addExtension(fetch);

         SRegRequest sRegRequest = SRegRequest.createFetchRequest();
         sRegRequest.addAttribute("fullname", true);
         sRegRequest.addAttribute("email", true);
         authReq.addExtension(sRegRequest);

         response.sendRedirect(authReq.getDestinationUrl(true));
      }
      catch (OpenIDException e)
      {
         _log.error(e.getMessage());
      }
   }

   private String _readOpenIdResponse(ActionRequest request, ActionResponse response)
   {
      try
      {
         ConsumerManager manager = OpenIdUtil.getConsumerManager();

         ParameterList paramList = new ParameterList(request.getParameterMap());

         DiscoveryInformation discovered = (DiscoveryInformation) request.getPortletSession().getAttribute(
               OpenIdKeys.OPENID_DISCOVERY);

         String receivingURL = request.getParameter("openid.return_to");

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
                     request.setAttribute(name, value);
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
}
