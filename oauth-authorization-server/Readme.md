# Auth server - Keycloak

## Users
- For login to Keycloak
  - url: host:8083/auth
  - user: bael-admin / pass
- There are two users registered in the Authorization Server:
  - john@test.com / 123
  - mike@other.com / pass


## Auth and authorization
### Options
- Security Assertion Markup Language (SAML) is an open standard that attempts to bridge the divide between authentication and authorization.
- OAuth is an open authorization standard.
- OpenID Connect is an authentication standard that superseded OpenId, runs on top of OAuth 2.0.



### Solution
We use OAuth for authorization and OpenID Connect for authentication.

### Docs
- https://www.keycloak.org/docs/latest/securing_apps/
- https://openid.net/specs/openid-connect-core-1_0.html#AuthorizationEndpoint
- https://robferguson.org/blog/2019/12/24/getting-started-with-keycloak/
- https://blog.hubspot.com/website/curl-command
- [jmeter form data](https://stackoverflow.com/questions/11250555/how-send-application-x-www-form-urlencoded-params-to-a-restserver-with-jmeter)
#### cookies subsection
- [short intro - how they work](https://stackoverflow.com/questions/8409705/how-do-javascript-cookies-get-sent-to-the-browser-and-how-are-they-verified)
