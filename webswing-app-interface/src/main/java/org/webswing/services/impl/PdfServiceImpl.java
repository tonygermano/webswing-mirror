package org.webswing.services.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.webswing.ext.services.PdfService;
import org.webswing.toolkit.util.Util;

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
		PDFGraphics2D graphics = new WebPdfGraphics2D(out, size);
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
			((PDFGraphics2D) pdfGrapthics).setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
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

	public static class WebPdfGraphics2D extends PDFGraphics2D {
		public WebPdfGraphics2D(File file, Dimension size) throws FileNotFoundException {
			super(file, size);
		}

		public WebPdfGraphics2D(File file, Component component) throws FileNotFoundException {
			super(file, component);
		}

		public WebPdfGraphics2D(OutputStream ros, Dimension size) {
			super(ros, size);
		}

		public WebPdfGraphics2D(OutputStream ros, Component component) {
			super(ros, component);
		}

		protected WebPdfGraphics2D(WebPdfGraphics2D graphics, boolean doRestoreOnDispose) {
			super(graphics, doRestoreOnDispose);
		}

		@Override
		public GraphicsConfiguration getDeviceConfiguration() {
			return Util.getWebToolkit().getGraphicsConfig();
		}

		public Graphics create() {
			try {
				writeGraphicsSave();
			} catch (IOException e) {
				handleException(e);
			}
			return new WebPdfGraphics2D(this, true);
		}

		public Graphics create(double x, double y, double width, double height) {
			try {
				writeGraphicsSave();
			} catch (IOException e) {
				handleException(e);
			}
			PDFGraphics2D graphics = new WebPdfGraphics2D(this, true);
			graphics.translate(x, y);
			graphics.clipRect(0, 0, width, height);
			return graphics;
		}

	};
}
