#Advanced Confginguration

##Embedding Webswing  into your page

Since Webswing version 2.2 it is possible to embed the Webswing's javascript in form of a snippet in your own webpage. Embedded Webswing will display the swing application in div element anywhere in your page. Copy the following snippet to your page: 

```html
<div 
 style="height:500px;" 
 data-webswing-instance="webswing" 
 data-webswing-options="{autoStart:true,  anonym:true,  args:'foo',  applicationName:'SwingSet3', connectionUrl:'http://<webswing-host-and-port>'}">
</div>

<script>
 (function (window, document) {
  var loader = function () {
   var script = document.createElement("script"), tag = document.getElementsByTagName("script")[0];
   script.src =  "http://<webswing-host-and-port>/javascript/webswing-embed.js";
   tag.parentNode.insertBefore(script, tag);
  };
  window.addEventListener ? window.addEventListener("load", loader, false) : window.attachEvent("onload", loader);
 })(window, document);
</script>
```
There are few things you need to configure in the snippet. Most important is to replace the 2  `<webswing-host-and-port>` placeholders with the location of your webswing deployment. For example `localhost:8080` if using the default settings. 

The `data-webswing-instance="my-webswing"` attribute tells the javascript to use this div element as webswing instance. Name of the instance `"my-webswing"` will be used as name for global object variable with functions controlling this instance. Following functions are exposed in this API: 

* `configure(options)` - configures the current instance. This function is processes the options specified in `data-webswing-options` if no argument is passed in. 
* `disconnect()` - disconnects the current webswing session, but leaving the swing application running. 
* `kill()` - disconnects the current webswing session with stopping the swing application. 
* `setControl(boolean)` - enables/disables the control of application. If set to false, no user events are sent to swing application.
* `start()` - This will initiate the connection to webswing server and start the swing application. If the `autoStart` is set to false or not defined in `data-webswing-options`, start function has to be called manually, otherwise the javascript will call start function automatically. 

The `data-webswing-options` attribute is used to configure the instance. 

* `autoStart:true`  - tells webswing to start the instance right away. If it is false, you need to call start() function manually as described above. 
* `anonym:true` -tells the server to authenticate the application with anonymous user. This user has access only to applications with 'authentication' and 'authorization' set to false in webswing.config file. 
* `args:'foo'` - for specifying additional arguments for the swing application. Value of this parameter will be appended to value specified in 'args' option of webswing.config file.
* `applicationName:'SwingSet3'`  - for selecting the application to run. If this parameter is specified, dialog with list of swing applications will be skipped. Name is the swing application name as specified in webswing.config file.
* `connectionUrl:'http://localhost:8080'` - tells the script where to look for webswing API. 

You can control the size of the div element where webswing is displayed with regular CSS . You can even change the size of the div dynamically and the webswing will automatically scale. 

If you are embedding webswing to page on different domain, you will have to enable Cross-origin resource sharing (`CORS`), either in `jetty.properties` file or using the `webswing.corsOrigins` system property. Set `*` to allow all domains, or use comma separated list of allowed domains. 

#Tomcat deployment
Even though webswing comes with embedded Jetty server, it is still possible to deploy it to external servlet container like Tomcat. Other J2EE servers should work as well, as far as they support Servlet 3.0 spec (but only Tomcat 8 is tested). To deploy webswing to external container you will have to configure few system properties either as JVM parameter at start up ( `-D` options ) or in case of Tomcat in `catalina.properties`:

```properties
webswing.warLocation=/path/to/tomcat8/webapps/webswing-server.war
webswing.usersFilePath=/path/to/user.properties
webswing.configFile=/path/to/webswing.config
webswing.tempDirBase=/path/to/tempfolder
#if want to embed to page hosted on different domain (set * for all)
webswing.corsOrigins=http://other.domain1, http://other.domain2 
```

Please note that the locations in demo `ebswing.config` are pointing to relative paths, so in order to make the demo applications running in tomcat you will need to change the paths.


