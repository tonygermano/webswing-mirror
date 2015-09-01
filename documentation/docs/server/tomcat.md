##Tomcat Deployment

Even though webswing comes with embedded Jetty server, it is still possible to deploy it to external servlet container like Tomcat. Other J2EE servers should work as well, as far as they support Servlet 3.0 spec (but only Tomcat 8 is tested). To deploy webswing to Tomcat, follow the following steps:

1. Create a new folder named `webswing` in Tomcat's home folder and unzip the webswing distribution to this folder.
2. Move `webswing-server.war` from distribution package to Tomcat's `webapps` folder.
3. In `conf/catalina.properties` file add the following properties. ( Or use `-D` jvm options to specify these properties) 

```properties
webswing.warLocation=tomcat8_home/webapps/webswing-server.war
webswing.usersFilePath=tomcat8_home/webswing/user.properties
webswing.configFile=tomcat8_home/webswing/webswing.config
webswing.tempDirBase=tomcat8_home/webswing/tmp
#if want to embed webswing to page hosted on different domain (set * for all)
webswing.corsOrigins=http://other.domain1, http://other.domain2 
```

>Please note that the locations in demo `webswing.config` are pointing to relative paths, so in order to make the demo applications running in tomcat you will need to change the paths accordingly


