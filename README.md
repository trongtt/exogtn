# GateIn-OpenID integration specification
OpenID is an open standard that describes how users can be authenticated in a decentralized manner, eliminating the need for services to provide their own ad hoc systems and allowing users to consolidate their digital identities.

OpenID authentication is now used and provided by several large websites. Providers include AOL, BBC, Google, IBM, MySpace, Orange, PayPal, VeriSign, LiveJournal, and Yahoo!

## GateIn Usecase
Nowadays, GateIn has used OrganizationService to authenticate a user, that mean user has to remember username and password for each production which based on GateIn. For usecase, I had a Google account, I want to use the account to log into GateIn system, I need only authenticate with Google service and then I can use GateIn as normally, user also can use OpenID account provided by several services such as Google, Yahoo, Paypal... to log into GateIn.

### GateIn-OpenID integration workflow

This is workflow for GateIn and OpenID integration, each step describe how a user acts on GateIn and response from the system.

<img src="https://github.com/ndkhoiits/exogtn/raw/3.2.x-openid/OpenID.jpg" />

The following section show detail for these step in above diagram:

- (1) User enter the OpenID to form, this is an URL provided by OP (stand for OpenID Provider) such as [Google], [Yahoo] ... GateIn discovers the OpenID and look up the necessary information for initiating requests such as OP endpoint URL and Protocol version. If OpenID identifier is not compatible with any OP, user has to enter the information again, otherwise go to step (2).

- (2) GateIn send a request to endpoint discovered at step (1) and some necessary information.

- (3) After GateIn send a request to OP, user will be required to authenticate by OP, and OP send a response to GateIn with some information in the response if successfully login.

- (4) GateIn receive the request sent by OP and handle the response in someways, if there are some errors in the request, user will be redirected to a login fail page, otherwise go to step (5).

- (5) GateIn checks the OpenID identifier, if the identifier has been already bound in an account, process login with the account in step (6), if no go to step (7).

- (7) User will be asked if he already had an account in GateIn system, if yes he enter GateIn username and password to login to portal and the OpenID identifier will be bound with this GateIn account at step (9), otherwise display a register form to enter the account information.

- (8) User enter information to register new GateIn account, if everything is going well, user will be logged in with just created account and bind the OpenID identifier to this account at step (9).

- (9) Bind an OpenID identifier to GateIn account, the relationship is many to one, that means a GateIn account can be bound by some OpenID identifiers while an OpenID identifier can bind to only one GateIn account.

## GateIn OpenID Technical Implementation

These step showed in above diagram has already been realized by implementation, OpenID can be considered as an extension for GateIn, that mean we don't need change so much in the legacy system. We created some servlet for handling these steps in the diagram.

- (1) (2) and (4) are handled by [`OpenIDConsumerServlet`]
   - The sevlet discovers the OpenID provider endpoint and send to that a request.

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

[google]: https://www.google.com/accounts/o8/id
[yahoo]: https://me.yahoo.com
[`OpenIDConsumerServlet`]: https://github.com/ndkhoiits/exogtn/blob/3.2.x-openid/openid/jar/src/main/java/org/exoplatform/openid/servlet/OpenIDConsumerServlet.java
