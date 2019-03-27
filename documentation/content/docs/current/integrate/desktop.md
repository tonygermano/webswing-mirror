---
title: "Desktop"
weight: 10
---

## File system

Swing application running inside Webswing are ultimately using the file system of the server where Webswing is running. This causes problems to applications that consume or generate files and interact with file system. 

Webswing solves this problem by providing an automatic `JFileChooser` integration dialog, which offers users ability to interact with server's file system by **uploading, downloading and deleting** files from the browser.

![file dialog](../img/filedialog.png)

**Permissions**

It is possible to disable upload, download or delete files in the `Security` tab of application settings in Admin console.

**Isolated file system**

If the users are not supposed to have access to the full servers file system, Webswing offers the option to limit the access to isolated folder that they are allowed to interact with. This setting `isolatedFs` can be set in `Security` tab of application settings in Admin console. If it is enabled, the user will only see folder `"upload"` in `homeDir` folder.

![isolated File System](../img/isolatedfs.png)

## Printing

Webswing implements seamless printing support, which works out of the box without any change needed in your application. 
Webswing generates a pdf from the printed document and opens it in a new browser tab using `PDF.js` viewer.

Printing integration handles printing using:

* Toolkit.getDefaultToolkit().getPrintJob() method
* PrinterJob.getPrinterJob()
* multi paged documents
* landscape and portrait orientation

Printing is demonstrated in SwingSet3 demo with sample source codes included. 

![isolated File System](../img/printing.png)

## Clipboard

Webswing has a built-in clipboard integration. Due to various browser security limitations some clipboard operations are limited at the moment. 

### Copy

When the user triggers copy operation from the Swing application by pressing `CTRL+c` or in menu option, Webswing analyzes the data written to clipboard and notifies the client what type of data is available in clipboard by small dialog.

Webswing recognizes 5 basic types. 

1. Plain text 
2. HTML  ![html copy dialog](../img/htmlcopy.png)
3. Image ![image copy dialog](../img/imagecopy.png)
4. Files ![file copy dialog](../img/filescopy.png)
5. Other (i.e. application specific types)

This dialog only informs the user that data is available in the Swing clipboard. To copy the `Plain text` or `HTML` type to local clipboard, the user has to press `ctrl+c` again while the dialog is visible. For copying the `image`, the user has to use the browser default behavior to copy the image to the local clipboard. 

If the Swing clipboard contains `files` and `allowDownload` option is enabled, the user will be presented with list links allowing him to download those files from the Swing application. 

>Note: `isolatedFs` option does not apply here. Access is not limited to files in the isolated folder.

### Paste

Paste operation can be triggered by `CTRL+v` keyboard shortcut. But here it gets a little tricky, because we have our Swing clipboard data sitting on the server and we have a local browser clipboard data. So we need to decide which one to use for the paste operation.    

The solution is simple. If the clipboard dialog is open, we use the Swing clipboard, because we know the user has just copied some data there. If the clipboard is not visible, we try to get the data from the local browser clipboard. 

![minimized copy dialog](../img/paste.png)

>Note: if paste operation is triggered from context menu, it will always use the Swing clipboard data. 

**Pasting from Swing clipboard**
All types are accessible to application if pasting from Swing clipboard. 

**Pasting from local browser clipboard**
See the following matrix for supported types based on browser.   

Browser | Supported content types
--------| -----------------------
Chrome  | Plain text, HTML, Images
Firefox | Plain text, HTML
IE      | Plain text, HTML


## java.awt.Desktop

The AWT framework Desktop class is the default interface for triggering OS specific operations like edit file or open link. Webswing implements this interface and interprets these commands in a web browser-friendly way. 

Method   |  Webswing operation
---------|--------------------
browse   | Opens the specified link in new tab
edit     | Referenced file is downloaded 
mail     | Opens a new email window in default mail client
open     | Referenced file is downloaded 
print    | Referenced file is downloaded 