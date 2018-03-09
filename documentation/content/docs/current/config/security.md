---
title: "Access Control"
weight: 5
---

## Security concept

Since version 2.5 Security can be configured for the server and per application separately.
Using different user base per application and for admin console access enables
easy multi-tenant deployments. 
 
To set up security for the server, use the "Configuration" page in admin console. 
Application security is part of application's config screen. 

Both server and application security use the same configuration options.

First, select the security module from the list of built-in modules. 

## Built-in modules 

| Module | Description |
| ------------- | ------------- |
| INHERITED | Application does not define its own security, but shares security context with server instead. This module is not available on the server.  |
| NONE | Enables anonymous access to application. No login is required. |
| EMBEDDED | Users, passwords and roles are defined directly in webswing.config configuration file.|
| PROPERTY_FILE | Users, passwords and roles are defined in the property file.|
| DATABASE | Users are stored in database. The library with JDBC driver has to be specified in security module's class path. Database connection details has to be defined.|
| OPENID_CONNECT | Single-sign-on using OpenID Connect protocol. |
| SAML2 | Single-sign-on using SAML2 protocol. |
| KEYCLOAK | Keycloak authentication server integration module. |
| Custom module | The custom security module can be created and fully-qualified class name defined.|


>In case you need help with security setup, contact support@webswing.org