package org.webswing.server.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
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
        String userId= (String) SecurityUtils.getSubject().getPrincipal();
        
        if (!fileMap.containsKey(fileId) || fileMap.get(fileId).file == null || !fileMap.get(fileId).file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
        
        if(userId==null || !userId.equals(fileMap.get(fileId).userId)){
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
        public String userId;
        public boolean temporary;

        public FileDescriptor(File file, String userId) {
            super();
            this.file = file;
            this.userId = userId;
        }
    }

    public static String registerFile(byte[] content, final String id, long validForTime, TimeUnit timeUnit, String validForClient) throws IOException {
        if (currentServlet != null) {
            String tempDir = System.getProperty(Constants.TEMP_DIR_PATH);
            File f = new File(URI.create(tempDir + "/" + id + ".pdf"));
            FileOutputStream output = new FileOutputStream(f);
            output.write(content);
            output.close();
            return registerFile(f, id, validForTime, timeUnit, validForClient, true);
        } else {
            throw new IOException("File servlet not yet initialized!");
        }
    }

    public static String registerFile(File file, final String id, long validForTime, TimeUnit timeUnit, String validForUser) throws IOException {
        return registerFile(file, id, validForTime, timeUnit, validForUser, false);
    }

    private static String registerFile(File file, final String id, long validForTime, TimeUnit timeUnit, String validForUser, boolean temp) throws IOException {
        if (currentServlet != null) {
            final FileDescriptor fd = new FileDescriptor(file, validForUser);
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
            return file.getAbsolutePath();
        } else {
            throw new IOException("File servlet not yet initialized!");
        }
    }

}
