package org.webswing.services.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStream;

import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.webswing.ext.services.PdfService;

public class PdfServiceImpl implements PdfService {

    private static PdfServiceImpl impl;

    public static PdfServiceImpl getInstance() {
        if (impl == null) {
            impl = new PdfServiceImpl();
        }
        return impl;
    }

    @Override
    public Graphics2D createPDFGraphics(OutputStream out, Dimension size) {
        PDFGraphics2D graphics = new PDFGraphics2D(out, size);
        graphics.setMultiPage(true);
        graphics.startExport();
        return graphics;
    }

    @Override
    public void startPagePDFGraphics(Graphics2D pdfGrapthics, Dimension size) {
        try {
            ((PDFGraphics2D) pdfGrapthics).openPage(size, "");
        } catch (IOException e) {
            ((PDFGraphics2D) pdfGrapthics).endExport();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endPagePDFGraphics(Graphics2D pdfGrapthics) {
        try {
            ((PDFGraphics2D) pdfGrapthics).closePage();
        } catch (IOException e) {
            ((PDFGraphics2D) pdfGrapthics).endExport();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closePDFGraphics(Graphics2D pdfGrapthics) {
        ((PDFGraphics2D) pdfGrapthics).endExport();
    }

}
