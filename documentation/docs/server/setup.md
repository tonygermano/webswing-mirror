## Startup options
Start scripts `start.bat` and `start.sh` are included in Webswing distribution. These scripts may need to be adapted to point to the right java installation and to configure other custom options. 

Starting webswing with option `-h` will print out help with list of all possible option. 
```
c:\webswing>java -jar webswing-server.war -h
```

You can define following options in start scripts:

Option						|Description						|Default value
----------------------------|-----------------------------------|-------------
 **-c <arg>**      | Configuration file name.			|`<webswing-server.war path>/webswing.config`
 **-d <arg>**                 | Create new temp folder for every webswing instance |`false`
 **-h <arg>**            | Local interface address where the web server will listen.|`localhost`
 **-j <arg>**           | Jetty startup configuration file.	|`./jetty.properties`
 **-kp <arg>**    | Keystore password.				|
 **-ks <arg>**       | Keystore file location for ssl configuration |
 **-p <arg>**            | Http port where the web server will listen. If 0 http is disable|`8080`
 **-s <arg>**         | Https port where the web server will listen.If 0 http is disabled.|`0`
 **-t <arg>**            | The folder where temp folder will be created for the webswing server|`./tmp`
 **-tp <arg>**  | Truststore password				|
 **-ts <arg>**     | Truststore file location for ssl configuration|
 **-u <arg>**           | Users properties file name. 		|`<webswing-server.war path>/users.properties`

---

## SSL configuration

There is a special configuration file for the built-in jetty called `jetty.properties`. It is used to configure connection options of the server like ports, protocols, SSL certificates and CORS origins (for embedded Webswing ). Command-line options have higher priority than this file. Location of this file can be specified by option `-j`. You can configure following options in this file: 

```properties
org.webswing.server.host=localhost

org.webswing.server.http=true
org.webswing.server.http.port=8080

org.webswing.server.https=true
org.webswing.server.https.port=8443
org.webswing.server.https.truststore=ssl/truststore.jks
org.webswing.server.https.truststore.password=123123
org.webswing.server.https.keystore=ssl/keystore.jks
org.webswing.server.https.keystore.password=123123

#comma separated urls
org.webswing.server.allowedCorsOrigins=*
```
