##Authentication setup
Webswing uses Apache Shiro framework for authentication and authorization. By default, the users are configured in simple properties file called `user.properties`, which is expected to be in the same folder as the `webswing-server.war` file. Location of this file could be changed using the `-u` command-line option.

This is the sample content of `user.properties` file:

```properties
#user.=[,role1][,role2]...
user.admin=pwd,admin
user.user=pwd,Printing
user.guest=guest
```

First line is a comment describing the format. Each user entry starts with prefix `user.`, followed by the actual user name. Password is specified after the equals sign. After the password comes comma separated list of roles for specifying the list of authorized applications. Role `admin` enables access to Administration console and implicit access to all applications. Role `Printing` refers to swing application name from `webswing.config` file.

---

##Custom Authentication provider with Shiro

Shiro framework offers also other methods of authentication like Ldap, Database or Active Directory. This could be configured in `shiro.ini` file which is located in the `webswing-server.war` archive in `WEB-INF` folder. Security manager realm implementation class name `org.webswing.server.util.WebSwingPropertiesRealm` has to be changed to one of the standard Shiro's implementations or your own custom. 
See:

* org.apache.shiro.realm.ldap.JndiLdapRealm 
* org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm 
* org.apache.shiro.realm.jdbc.JdbcRealm

```properties
[main]
authc=org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter
authc.loginUrl = /login

webswingUserProps=org.webswing.server.util.WebSwingPropertiesRealm
securityManager.realm = $webswingUserProps

[urls]
/async/admin/** = authc, roles[admin]
/async/swing = authc
/file = authc
/upload = authc
/logout = logout
/** = anon 
```

For detailed information please refer to Shiro documentation or if you need help with configuration please contact us.
