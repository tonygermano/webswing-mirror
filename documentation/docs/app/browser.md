##Webswing URL parameters
When you start Webswing it will be available on `http://localhost:8080`. The page served on this URL (`index.html`) allows you to change some of the default behavior using URL parameters. 

Following configurations are available by default:

### Preselected aplication (skip app selection screen)
[http://localhost:8080?app=SwingSet3](http://localhost:8080?app=SwingSet3) : for selecting the application to run. If this parameter is specified, dialog with list of swing applications will be skipped. `<name>` is the swing application name as specified in `webswing.config` file.

### Additional application arguments
[http://localhost:8080?args=arg1,arg2](http://localhost:8080?args=arg1,arg2) : for specifying additional arguments for the swing application. Value of this parameter will be appended to value specified in `args` option of `webswing.config` file.

### Anonymous access (skip login screen)
[http://localhost:8080?anonym=true](http://localhost:8080?anonym=true) : tells the server to authenticate the application with anonymous user. This user has access only to applications with `authentication` and `authorization` set to `false` in `webswing.config` file. 

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

<script data-webswing-global-var="webswingControll">
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

The `data-webswing-instance="my-webswing"` attribute tells the javascript to use this div element as webswing instance. Name of the instance `"my-webswing"` will be used as name for global object variable with functions controlling this instance. Following functions are available in this global object: 

* **`configure(options)`** - configures this instance. This function read the options from `data-webswing-options` attribute if no argument is passed in. Argument `options` is object with following properties:
	* `autoStart`  - tells webswing to execute configure() and start() right after the instance is initialized. If it is false, start() function has to be triggered manually. This option only valid if set up in `data-webswing-options` attribute
    * `applicationName` - Preselects the swing application and skips selection screen
    * `args` - additional java application arguments. Appended to those defined in configuration.
    * `anonym` - skip login for applications that allows anonymous access (default:false)
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