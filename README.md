# GateIn-OpenID integration specification

Almost features of GateIn required user sign in before using availability, and to sign in GateIn, user must use an account that was registered and stored in GateIn account database. Sometimes, user only want to visit some lightly features that is not too important to spend much time for registering a new account. Solution here is OpenID, user will use an OpenID account from trusted OpenID provider (OP) to sign in GateIn.

Some information about OpenID protocol from http://openid.net/ as:OpenID is an open, decentralized, free framework for user-centric digital identity.OpenID starts with the concept that anyone can identify themselves on the Internet the same way websites do-with a URI (also called a URL or web address). Since URIs are at the very core of Web architecture, they provide a solid foundation for user-centric identity.

Some advantages of using OpenID are don't need to store passwords in GateIn, GateIn only automatically create new account and map to OpenID account at the first sign in of User. GateIn also is easier to admistrate account profiles

# GateIn OpenID Implementation

### We need to do something

- UI
   - A Portlet to render necessary User Interface to help User be able to 
     click&type OpenID credentials during sign in progress
- Handler
   - A handler to handle request/response between GateIn and an OpenID provider during authentication progress.
     This handler will use OpenID4Java open source project in backend to make authentication flow as OpenID protocol.
   - A handler to process mapping an OpenID to GateIn account.
- Service
   - Map an OpenID to GateIn account.
   - Remove an OpenID from GateIn account
   - Find a GateIn account by OpenID and get all OpenID owned by GateIn account

### What was done

Now the UI and Handlers are considered servlets

- `OpenIDConsumerServlet` for enter an OpenID, process request and response from OpenID provider
   - The sevlet discovers the OpenID provider endpoint and send to that a request.
   - Check response status from OP, create token and store in session
   - Process login if OpenID already bound to GateIn account, otherwise redirect to register servlet
- `OpenIDRegisterServlet` for create new GateIn account
- `OpenIDMapServlet` for authentication logged account and mapping to the OpenID identity

All information passed between servlets stored in session because we're only using servlet for display and handle request, it's bad design and need improve

- `OpenIDService` service provided some utilities such as map or remove an OpenID from GateIn account, find all OpenID bound in GateIn account and find an GateIn account by OpenID. This interface has been implemented by `OpenIDServiceImpl` for now.
- `OpenIDDAO` service map an OpenID with GateIn account. It has been implemented by `OpenIDDAOMemoryImpl` and all mapping are stored in memory

### What have to do

- Change the way to display and handle request for OpenID login. Should use portlet for display.
- Move source code changes to `openid` project as an extension as much as possible.
- Persist the OpenID - GateIn account mapping in DB or any persistent storage.
- Provide an UI for manage OpenID as a profile