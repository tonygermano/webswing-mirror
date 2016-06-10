## Swing Application setup

Swing applications are configured in json format and by default saved in file `webswing.config`, which is by default placed in the same folder as the `webswing-server.war` file. This location may be changed using the `-c` command-line option. It is recommended to use Administration console's form based configuration screen to modify this file to avoid json formatting problems.

---

## Using Admin console

Easiest way of configuring your application is using Admin console. For accessing Admin console add `/admin` path to your webswing url (ie. [http://localhost:8080/admin](http://localhost:8080/admin)).

Then go to `Settings` -> `Server configuration` -> `Swing applications`. You can see the list of configured applications. You can add new or edit existing applications here. 

![Swing application list ](img/app-list.png) 

By selecting one of the application in the list it will expand the configuration panel where you can edit or view the settings. These are split in 3 panels:

![Swing general settings ](img/general-settings.png) 

Each setting has a description, which is displayed when hovered on the question mark.

![Swing java settings](img/help.png) 

---

## JSON format description

Here is a sample `webswing.config` file content with demo swing application configured:
```json
{
  "applications" : [{
    "name" : "SwingSet3",
    "icon" : "${user.dir}/demo/SwingSet3/icon.png",
    "jreExecutable": "${java.home}/bin/java",
    "javaVersion": "${java.version}",
    "mainClass" : "com.sun.swingset3.SwingSet3",
    "classPathEntries" : [ "${user.dir}/demo/SwingSet3/SwingSet3.jar", "${user.dir}/demo/SwingSet3/lib/*.jar" ],
    "vmArgs" : "-Xmx128m -DauthorizedUser=${user}",
    "args" : "",
    "homeDir" : "demo/SwingSet3/${user}",
    "maxClients" : 1,
    "antiAliasText" : true,
    "swingSessionTimeout" : 300,
    "sessionMode": "CONTINUE_FOR_USER",
    "allowStealSession": true,
    "authorization" : false,
    "isolatedFs" : true,
    "debug" : true,
    "authentication" : false,
    "directdraw" : false,
    "allowDelete" : true,
    "allowDownload" : true,
    "allowAutoDownload" : true,
    "allowUpload" : true,
    "allowJsLink" : true
  }]
}
```

---

## Variable resolution

Most of the text options support variable replacement. Available variables are java system properties, os environment variables and set of special webswing vartiables. List of all available variables and their values are available in Admin console. Variables are specified with dolar folowed by variable name in curly brackets. For example `${variable_name}`.

In aditions to variables, for `classPathEntries` properties it is possible to use wildcard characters. Supported wildcards are `*` (everything) and `?` (any singe character). 

![Variables in Admin console](img/variables.png)
 


**Available variables:**

Variable Name 				| Description
----------------------------|------------
**`${user}`**					| Webswing specific logged in user name.
**`${clientId}`** 				| Webswing specific unique browser identifier. 
**`${clientIp}`**				| IP address of browser which started this application.
**`${clientLocale}`**			| Locale of browser which started this application.
**`${customArgs}`**				| Custom Arguments specified in URL parameters. [See details](browser/#additional-application-arguments)
**Java system Properties** 		| All properties accessible to server's JVM using System.getProperty method
**System environment variables**| All OS level environment variables accessible to script that started webswing server JVM. 

In Admin console options with variable replacement support appears with a flash icon. When focused a panel with resolved value of is displayed:

![Variables resolution](img/resolve-var.png)

---

##Settings overview

`name`: Swing application name. Will be displayed in application selection screen.

`icon`: Path to icon displayed in application selection dialog. Absolute path or path relative to `homeDir`

`jreExecutable`: Path to java executable that will be used to spawn swing application process. Java 6,7 and 8 is supported. ([More details](#custom-swing-startup-script))

`javaVersion`: Java version of the JRE executable defined in `jreExecutable`. Expected values are starting with '1.6', '1.7' or '1.8'.

`mainClass`: Swing application fully qualified class name. (ie. 'com.mypackage.Main')

`classPathEntries`: Swing application's classpath. Absolute or relative path to jar file or classes directory. At least one classPath entry should be specified containing the main class. Allows using `?` (any single char) and `*` (everything) wildcards.

`vmArgs`: Commandline arguments processed by Oracle's Java Virtual Machine. (ie. '-Xmx128m')

`args`: Swing application main method arguments. This string will be passed to the main method's (String[] args)

`homeDir`: Swing application's home directory. Swing application instances will be executed from this directory. This will also be the base directory of any relative classpath entries specified.

`maxClients`: Maximum number of allowed simultaneous connections for this application.

`antiAliasText`: Enables rendering of anti-aliased text. Smoothens the edges of the text.

`swingSessionTimeout`: Specifies how long (seconds) will be the swing application left running after the user closes the browser. User can reconnect in this interval and continue in last session.

`sessionMode`: Select session behavior when user reconnects to application. Available options: 

1. `ALWAYS_NEW_SESSION`: New swing application is started for every Webswing session. (Session timeout will be set to 0)
2. `CONTINUE_FOR_BROWSER`: Webswing session can be resumed **in the same browser** after connection is terminated (Session timeout applies).
3. `CONTINUE_FOR_USER`: Swing session can be resumed **by the same user** from any computer after the connection is terminated(Session timeout applies).

`theme`: Select one of the default window decoration themes or a enter path to a XFWM4 theme folder.

`authentication`: If set to `false`, the application will be accessible for anonymous user. If `true` only authenticated user is allowed to use this application. False setting will be ignored if `authorization` option is `true`.

`authorization`: Set authorized access to this application. Only users with role same as application's name can access this application.

`isolatedFs`: If enabled, this setting will force the JFileChooser to stay inside isolated folder. The new isolated folder is created in `${homeDir}/upload` 

`debug`: Enables remote debugging for this application. After the application is started with `?debugPort=8000` url parameter from browser, it will wait for remote debugger connection on port 8000

`directdraw`: Activates the new rendering method if the clients browser supports the required technologies (typed arrays, web socket). This feature is experimental state at the moment. For more information on this method refer to Development guide. 

`allowDelete`: This options activates the 'Delete' button on the JFileChooser integration panel. If this is true, user will be allowed to delete files displayed in JFileChooser dialog.

`allowDownload`: This options activates the 'Download' button on the JFileChooser integration panel. If this is true, user will be allowed to download files displayed in JFileChooser dialog.

`allowAutoDownload`: If selected, the JFileChooser dialog's save mode will trigger file download as soon as the selected file is available on filesystem.

`allowUpload`: This options activates the 'Upload' button and drop area on the JFileChooser integration panel. If this is true, user will be allowed to upload files to folder displayed in JFileChooser dialog.

`allowJsLink`: If selected, the JSLink feature will be enabled, allowing swing application to invoke javascript and vice versa. (See `netscape.javascript.JSObject`)

---

##Custom Swing startup script

Sometimes it is necessary to prepare the environment before the Swing process is started. This may include steps like 
changing current working directory or using sudo to run Swing as different user. This can be achieved by pointing the `jreExecutable` 
option to custom startup script. 

Custom Swing startup script must follow few rules in order to work with Websing: 

1. Last step of the script should execute java with the arguments as passed in by webswing. (ie. `$JAVA_HOME/bin/java $@` )
2. If the script has arguments of its own, they should be shifted before calling java (`shift 3` if your script uses 3 arguments)
3. Be aware that variable resolution in `webswing.config` is done in servers context. (the evironment changes will not be reflected to variables defined in webswing.config)

Here is an example of custom script that will use `sudo` to run the swing process as logged in user. We assume that users defined in `users.properties` have os level counterparts
and the user used to start the server is properly configured in `sudoers` (needs NOPASSWD flag in sudoers - see man page). 

Here is our application configuration:
```json
{
    "name" : "SwingSet3",
    "jreExecutable": "startSwingSet3.sh ${user}",
    "javaVersion": "1.8",
    "mainClass" : "com.sun.swingset3.SwingSet3",
    "classPathEntries" : [ "SwingSet3.jar", "lib/*.jar" ],
    "vmArgs" : "-Xmx128m",
    "args" : "",
    "homeDir" : "demo/SwingSet3"
}
```

When Webswing will start a Swing application with above configuration, commandline will look like this:

```
startSwingSet3.sh johnDoe -Xmx128m <webswing specific configuration> -cp webswing-server.war main.Main
```

Now the custom script `demo/SwingSet3/startSwingSet3.sh` that runs the java process as logged in user will look like following:

```sh
#!/bin/sh
#save user to temporary variable
USER=$1
#shift the arguments by one - the user
shift
#run java with sudo 
sudo -u $USER /home/work/jdk/jdk/bin/java $@ 
```




