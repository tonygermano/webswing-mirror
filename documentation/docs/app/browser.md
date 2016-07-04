##Webswing URL parameters
When you start Webswing it will be available on `http://localhost:8080`. The page served on this URL (`index.html`) allows you to change some of the default behavior using URL parameters. 

Following configurations are available by default:

### Preselected aplication (skip app selection screen)
[http://localhost:8080?app=SwingSet3](http://localhost:8080?app=SwingSet3) : for selecting the application to run. If this parameter is specified, dialog with list of swing applications will be skipped. `<name>` is the swing application name as specified in `webswing.config` file.

### Additional application arguments
[http://localhost:8080?args=arg1 arg2 "arg3 and 4"](http://localhost:8080?args=arg1 arg2 "arg3 and 4") : for specifying additional arguments for the swing application. Value of this parameter will be available in `${customArgs}` variable that can be used in `webswing.config` file ( ie. `"args": "${customArgs}"`). 
You can specify the arguments as you would in commandline. The example URL would resolve to 3 arguments in your main method: `arg1` ,`arg2` and `arg3 and 4`.

### Anonymous access (skip login screen)
[http://localhost:8080?anonym=true](http://localhost:8080?anonym=true) : tells the server to authenticate the application with anonymous user. This user has access only to applications with `authentication` and `authorization` set to `false` in `webswing.config` file. 

### Disable binary socket
[http://localhost:8080?binarySocket=false](http://localhost:8080?binarySocket=false) : Binary socket is enabled by default. If you wish to use json for transfering data through websocket, set this option to false.

### Session recording
[http://localhost:8080?recording=true](http://localhost:8080?recording=true) : if recording is enabled, webswing will create a recording file in temp foler, which can be later used for replay of the application frame by frame just like a movie.

### Session playback 
[http://localhost:8080?recordingPlayback=path/to/recordingFile.wss](http://localhost:8080?recordingPlayback=path) : This option can be used to play the recording file. This option is internaly used in Admin console for playback of finished sessions. Only users with admin role are allowed to playback recordings.

### Debugging 
[http://localhost:8080?debugPort=8000](http://localhost:8080?debugPort=8000) : See [debuging guide](../dev/development.md#debugging)


 

URL parameters can be combined together.

If your deployment has other specific requirements which are not achievable by this set of parameters, you can embed Webswing's javascript snippet to your own web page, where you have full control and ability to further customize the behavior.

##Embeding to web page

With Webswing it is possible to embed your swing application to your own webpage. Good example is the default Webswing page or Admin console mirror view feature, where Webswing javascript is embeded in same like in below snippet. You can copy this snippet to your webpage: 

```html
<div 
 style="height:500px;" 
 data-webswing-instance="webswing" 
 data-webswing-options="{autoStart:true,  anonym:true,  args:'foo',  applicationName:'SwingSet3', connectionUrl:'http://<webswing-host-and-port>'}">
</div>

<script>
	(function (window, document) {
	  var loader = function () {
		//fix for ie
		if (!window.location.origin) {
			window.location.origin = window.location.protocol + "//" + window.location.hostname
					+ (window.location.port ? ':' + window.location.port : '');
		}
		var xmlhttp = new XMLHttpRequest();
	    xmlhttp.onreadystatechange = function() {
	        if (xmlhttp.readyState == XMLHttpRequest.DONE ) {
	        	var version = xmlhttp.status == 200 ? xmlhttp.responseText : "undefined";
	        	var script = document.createElement("script"), tag = document.getElementsByTagName("script")[0];
	    	    script.src =  document.location.origin + document.location.pathname + "javascript/webswing-embed.js?version="+version;
	    	    tag.parentNode.insertBefore(script, tag);
	        }
	    };
	    xmlhttp.open("GET", document.location.origin + document.location.pathname +"rest/webswing/version", true);
	    xmlhttp.send();
	  };
	  window.addEventListener ? window.addEventListener("load", loader, false) : window.attachEvent("onload", loader);
	})(window, document);
</script>
```
There are few things you need to configure in the snippet. Most important is to replace the 2  `<webswing-host-and-port>` placeholders with the location of your webswing deployment. For example `localhost:8080` if using the default settings. 

The `data-webswing-instance="my-webswing"` attribute tells the javascript to use this div element as webswing instance. Name of the instance `"my-webswing"` will be used as name for global object variable with functions controlling this instance. Following functions are available in this global object: 

* **`configure(options)`** - configures this instance. This function read the options from `data-webswing-options` attribute if no argument is passed in. Argument `options` is object with following properties:
	* `autoStart`  - tells webswing to execute configure() and start() right after the instance is initialized. If it is false, start() function has to be triggered manually. This option only valid if set up in `data-webswing-options` attribute
    * `applicationName` - Preselects the swing application and skips selection screen
    * `args` - additional java application arguments. Appended to those defined in configuration.
    * `binarySocket` - use binary transfer in websocket - saves bandwidth (default:true)
    * `recording` - record this application session (default:false)
    * `clientId` - set the clientId, used with mirror session or to continue running session. 
    * `mirror` - for starting a mirror session. Only admin role is allowed to use this (default:false)
    * `connectionUrl` - base url for connecting to websocket service. (default: current location url) 
    * `debugPort` - integer that specifies on which port should the debugger listen. See development docs for more information.
    * `javaCallTimeout` - JsLink java method invocation timeout. If java method is not returned with result, error is logged to browser console.  
    * `recordingPlayback` - File for session recording playback. Only admin role alowed to use this.
    * `appletParams` - Applet parameters. Ignored if swing application is not applet.
* **`disconnect()`** - disconnects the current webswing session, but leaving the swing application running. 
* **`kill()`** - disconnects the current webswing session with stopping the swing application. 
* **`setControl(boolean)`** - enables/disables the control of application. If set to false, no user events are sent to swing application.
* **`start()`** - This will initiate the connection to webswing server and start the swing application. If the `autoStart` is set to false or not defined in `data-webswing-options`, start function has to be called manually, otherwise the javascript will call start function automatically. 

The `data-webswing-options` attribute is used to configure the instance. Javascirpt object notation is expected with properties listed in `configure(options)` function description. 

You can control the size of the div element where webswing is displayed with regular CSS . You can even change the size of the div dynamically and Webswing canavs will automatically scale. 

If you are embedding webswing to page on different domain, you will have to enable Cross-origin resource sharing (`CORS`), either in `jetty.properties` file or using the `webswing.corsOrigins` system property. Set `*` to allow all domains, or use comma separated list of allowed domains. 


##Bootstraping Webswing javascript 

When the Webswing javascript is loaded it will scan the DOM for div elements with attribute `data-webswing-instance` and will initialize them automatically. For use cases when webswing instance has to be started later it is possible to export global webswing variable exposing the bootstraping API. 
By definig attribute `data-webswing-global-var="webswingControl"` on the `<script>` element of embeded snippet, you tell webswing to create a global variable called `webswingControl` which has following functions: 

* **`scan()`** - re-runs the full DOM search for `data-webswing-instance` attribute.
* **`bootstrap(divElement)`** - instantiate webswing in div element specified in argument. 