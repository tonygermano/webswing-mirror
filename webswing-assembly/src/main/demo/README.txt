Webswing comes with an embeded jetty web server so to start it just 
execute start.bat(.sh), open browser (IE9+,Chrome,FF) and load http://localhost:8080

You can also use following url arguments with default built-in page (or any combination):
http://localhost:8080?app=SwingSet3  - for pre selecting the application to run
http://localhost:8080?args=arg1%20arg2 - for additional application start arguments (passed to main methods as args[] param)
http://localhost:8080?anonym=true - for skipping the login process (user will be automatically logged in as anonymous user)


In case you want to deploy in Tomcat 8, add the following properties to your catalina.properties file:
webswing.warLocation=/path/to/tomcat8/webapps/webswing-server.war
webswing.usersFilePath=/path/to/user.properties
webswing.configFile=/path/to/webswing.config
webswing.tempDirBase=/path/to/tempfolder
#if want to embed to page hosted on different domain (set * for all)
webswing.corsOrigins=http://other.domain1, http://other.domain2


To embed webswing into your web page, just add following snippet (for all available options check the online documentation):
<div style="height:500px;" data-webswing-instance="webswing" data-webswing-options="{autoStart:true, anonym:true, args='foo', applicationName:'SwingSet3', connectionUrl:'http://<webswing-host-and-port>'}"></div>
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