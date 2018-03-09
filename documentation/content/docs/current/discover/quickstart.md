---
title: "Quick Start Guide"
weight: 20
---


This step by step guide will help you install and start the Webswing server and set up your swing application in 5 minutes.

## Prerequisites

1. Java 8 installed
2. Modern browser installed  

For the purpose of this guide, we will use a Windows-based box. Linux installation is covered on a separate page. 

## Starting the Server
Download the distribution from the [Webswing home page](http://www.webswing.org) and unzip it to a new folder. For demonstration we use `C:\webswing`

![unzipped Webswing distribution](../img/unzipped-distribution.png)

Open a command prompt and make sure you are using the current Java executable. (Verify by running *java -version*)

Double click on the `webswing.bat` file to start the Webswing server.

![started Webswing server](../img/webswing-running.png)

## Running Demo Application
Open your browser and navigate to address [http://localhost:8080](http://localhost:8080)  
Login using the default username and password `admin` and `pwd`.

![Webswing login screen](../img/login-screen.png)
 
You should see the following screen with 4 demo applications.  
You can try one of the demo apps by clicking the icon. 

![application selector screen](../img/app-selector.png)

After starting the SwingSet3 application you should see the following screen:

![Webswing SwingSet3 running](../img/swing-running.png)

## Setup Your Own Application
For this demonstration we use the [CelsiusConverter](https://docs.oracle.com/javase/tutorialJWS/samples/uiswing/CelsiusConverterProject/CelsiusConverter.jar) application from Oracle Swing tutorial.
The CelsiusConverter.jar has been put in the `C:/webswing` folder. 

Open your browser and navigate to address [http://localhost:8080](http://localhost:8080)  
To setup your application, click the *Manage* button located on the top right corner - this navigates you to Admin Console [http://localhost:8080/admin](http://localhost:8080/admin).

![application selector screen](../img/app-selector.png)

In the admin console, click the *Create New App" located on the top right side.

![admin console screen](../img/admin-console.png)

Enter the path of your app. This is the HTTP URL path part under which your app will be available.

![Enter Path](../img/create-newapp.png)

Scroll down, locate *My Application* and click *Show Config* on the right side.

![New Application](../img/my-app.png)

For the quick setup:  

1. adjust section *3. Application - Java*:  
   add CelsiusConverter.jar to the *Classpath field*  
   add learn.CelsiusConverterGUI to the *Main Class* field.
   ![Application settings](../img/my-app2.png)
2. Click Apply on the left top
3. Click Enable on the right top
4. Navigate to path as entered on the beginning [http://localhost:8080/myapp](http://localhost:8080/myapp)
  * The application should be also available under the new icon within the main menu [http://localhost:8080](http://localhost:8080)

In case the application failed to start, search for the problem in the log file `webswing.log` located in the Webswing home folder - in our case `C:\webswing`.   