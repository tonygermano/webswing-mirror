package org.webswing.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.Constants;

public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = 7829511263519944733L;
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    private static FileServlet currentServlet = null;
    private Map<String, FileDescriptor> fileMap = new HashMap<String, FileDescriptor>();
    private ScheduledExecutorService invalidatorService = Executors.newSingleThreadScheduledExecutor();

    public void init() throws ServletException {
        currentServlet = this;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileId = request.getParameter("id");
        String clientId= null;
        for(Cookie c:request.getCookies()){
            if(Constants.CLIENT_ID_COOKIE.equals(c.getName())){
                clientId=c.getValue();
            }
        }
        if (!fileMap.containsKey(fileId) || fileMap.get(fileId).file == null || !fileMap.get(fileId).file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
        
        if(clientId==null || !clientId.equals(fileMap.get(fileId).clientId)){
            response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403.
            return;
        }
        
        File file = fileMap.get(fileId).file;
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            close(output);
            close(input);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        //TODO: delete temp files
    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class FileDescriptor {

        public File file;
        @SuppressWarnings("unused")
        public Future<?> invalidateScheduleTask;
        public String clientId;
        public boolean temporary;

        public FileDescriptor(File file, String clientId) {
            super();
            this.file = file;
            this.clientId = clientId;
        }
    }

    public static void registerFile(byte[] content, final String id, long validForTime, TimeUnit timeUnit, String validForClient) throws IOException {
        if (currentServlet != null) {
            String tempDir = System.getProperty(Constants.TEMP_DIR_PATH, System.getProperty("java.io.tmpdir"));
            File f = new File(tempDir + File.separator + id + ".pdf");
            FileOutputStream output = new FileOutputStream(f);
            output.write(content);
            output.close();
            registerFile(f, id, validForTime, timeUnit, validForClient, true);
        } else {
            throw new IOException("File servlet not yet initialized!");
        }
    }

    public static void registerFile(File file, final String id, long validForTime, TimeUnit timeUnit, String validForClient) throws IOException {
        registerFile(file, id, validForTime, timeUnit, validForClient, false);
    }

    private static void registerFile(File file, final String id, long validForTime, TimeUnit timeUnit, String validForClient, boolean temp) throws IOException {
        if (currentServlet != null) {
            final FileDescriptor fd = new FileDescriptor(file, validForClient);
            fd.temporary = temp;
            synchronized (currentServlet.fileMap) {
                currentServlet.fileMap.put(id, fd);
                if (validForTime > 0) {
                    Future<?> invalidateTask = currentServlet.invalidatorService.schedule(new Runnable() {

                        @Override
                        public void run() {
                            currentServlet.fileMap.remove(id);
                            if (fd.temporary) {
                                fd.file.delete();
                            }
                        }
                    }, validForTime, timeUnit);
                    fd.invalidateScheduleTask = invalidateTask;
                }
            }
        } else {
            throw new IOException("File servlet not yet initialized!");
        }
    }

}
