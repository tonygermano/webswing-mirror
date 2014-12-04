package org.webswing.util;

import org.webswing.ext.services.ImageService;
import org.webswing.ext.services.PdfService;
import org.webswing.ext.services.ServerConnectionService;
import org.webswing.ext.services.SwingClassLoaderFactoryService;

public class Services {

    private static ImageService imageService;
    private static PdfService pdfService;
    private static ServerConnectionService serverService;
    private static SwingClassLoaderFactoryService classloaderService;

    protected static void initialize(ImageService imageServiceImpl, PdfService pdfServiceImpl, ServerConnectionService serverServiceImpl, SwingClassLoaderFactoryService classloaderServiceImpl) {
        imageService = imageServiceImpl;
        pdfService = pdfServiceImpl;
        serverService = serverServiceImpl;
        classloaderService = classloaderServiceImpl;
    }

    public static ImageService getImageService() {
        if (imageService == null) {
            Logger.fatal("ImageService has not been initialize. Exiting...");
            System.exit(1);
        }
        return imageService;
    }

    public static PdfService getPdfService() {
        if (pdfService == null) {
            Logger.fatal("PdfService has not been initialize. Exiting...");
            System.exit(1);
        }
        return pdfService;
    }

    public static ServerConnectionService getConnectionService() {
        if (serverService == null) {
            Logger.fatal("Connection service has not been initialize. Exiting...");
            System.exit(1);
        }
        return serverService;
    }

    public static SwingClassLoaderFactoryService getClassloaderService() {
        if (classloaderService == null) {
            Logger.fatal("Classloader service has not been initialize. Exiting...");
            System.exit(1);
        }
        return classloaderService;
    }

}
