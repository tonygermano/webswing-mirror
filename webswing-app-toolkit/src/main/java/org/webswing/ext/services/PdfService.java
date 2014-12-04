package org.webswing.ext.services;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.OutputStream;

public interface PdfService {

    Graphics2D createPDFGraphics(OutputStream out, Dimension size);

    void startPagePDFGraphics(Graphics2D pdfGrapthics, Dimension size);

    void endPagePDFGraphics(Graphics2D pdfGrapthics);

    void closePDFGraphics(Graphics2D pdfGrapthics);

}
