package org.webswing.server.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.webswing.Constants;
import org.webswing.model.c2s.JsonEventUpload;
import org.webswing.model.c2s.JsonEventUpload.UploadType;
import org.webswing.server.SwingInstanceManager;

public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = -7543155891431302443L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String clientId = request.getParameter("clinetId");
        if (clientId != null) {
            Part filePart = request.getPart("files[]"); // Retrieves <input type="file" name="file">
            String filename = getFilename(filePart);
            String tempName = UUID.randomUUID().toString();
            InputStream filecontent = filePart.getInputStream();
            String tempDir = System.getProperty(Constants.TEMP_DIR_PATH);
            File f = new File(URI.create(tempDir + "/" + tempName));
            FileOutputStream output = new FileOutputStream(f);
            IOUtils.copy(filecontent, output);
            output.close();
            filecontent.close();
            JsonEventUpload msg=new JsonEventUpload();
            msg.type=UploadType.Upload;
            msg.fileName=filename;
            msg.tempFileLocation=f.getAbsolutePath();
            boolean sent = SwingInstanceManager.getInstance().sendMessageToSwing(null, clientId,msg);
            if(!sent){
                f.delete();
            } else { 
                resp.getWriter().write("{\"files\":[{\"name\":\""+filename+"\"}]}"); //TODO size
            }
        }
    }

    private static String getFilename(Part part) {
        for (String cd : part.getHeader("Content-Disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }

}
