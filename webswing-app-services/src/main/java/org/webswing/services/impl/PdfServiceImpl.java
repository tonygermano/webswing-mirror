package org.webswing.services.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;

import org.freehep.graphicsbase.util.UserProperties;
import org.freehep.graphicsio.PageConstants;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.webswing.ext.services.PdfService;
import org.webswing.toolkit.WebPrinterJobWrapper;
import org.webswing.toolkit.util.DummyGraphics2D;
import org.webswing.toolkit.util.Util;

public class PdfServiceImpl implements PdfService {

	private static PdfServiceImpl impl;
	private static Graphics2D dummyG = new DummyGraphics2D();

	public static PdfServiceImpl getInstance() {
		if (impl == null) {
			impl = new PdfServiceImpl();
		}
		return impl;
	}
	
	@Override
	public void printToPDF(OutputStream out, Pageable pageable, Printable printable, PrintRequestAttributeSet attribs) throws PrinterException, IOException {
		PageFormat pageFormat = WebPrinterJobWrapper.toPageFormat(attribs);
		Graphics2D resultPdf = createPDFGraphics(out, pageFormat);
		
		if (printable != null) {
			int i = 0;
			while (paintPdf(resultPdf, pageFormat, attribs, printable, i++) != Printable.NO_SUCH_PAGE) {
			}
		} else if (pageable != null) {
			int no = pageable.getNumberOfPages();
			for (int i = 0; i < no; i++) {
				PageFormat pageablePageFormat = pageable.getPageFormat(i);
				paintPdf(resultPdf, pageablePageFormat != null ? pageablePageFormat : pageFormat, attribs, pageable.getPrintable(i), i);
			}
		}
		closePDFGraphics(resultPdf);
	}

	private int paintPdf(Graphics2D resultPdf, PageFormat pageFormat, PrintRequestAttributeSet attribs, Printable printable, int i) throws PrinterException {
		if (isInRange(i, attribs)) {
			if (printable != null && printable.print(dummyG, pageFormat, i) != Printable.NO_SUCH_PAGE) {
				startPagePDFGraphics(resultPdf, pageFormat);
				int result = printable.print(resultPdf, pageFormat, i);
				endPagePDFGraphics(resultPdf);
				return result;
			}
			return Printable.NO_SUCH_PAGE;
		}
		return printable.print(dummyG, pageFormat, i);
	}
	
	private Graphics2D createPDFGraphics(OutputStream out, PageFormat format) {
		UserProperties props = createPdfProperties(format);
		PDFGraphics2D graphics = new WebPdfGraphics2D(out, getPageDimension(props));
		graphics.setProperties(props);
		graphics.setMultiPage(true);
		graphics.startExport();
		return graphics;
	}

	private void startPagePDFGraphics(Graphics2D pdfGrapthics, PageFormat format) {
		try {
			PDFGraphics2D g = (PDFGraphics2D) pdfGrapthics;
			UserProperties props = createPdfProperties(format);
			g.setProperties(props);
			g.openPage(getPageDimension(props), null);
			g.clipRect(format.getImageableX(),format.getImageableY(),format.getImageableWidth(),format.getImageableHeight());
		} catch (IOException e) {
			((PDFGraphics2D) pdfGrapthics).endExport();
			throw new RuntimeException(e);
		}
	}
	
	private void endPagePDFGraphics(Graphics2D pdfGrapthics) {
		try {
			((PDFGraphics2D) pdfGrapthics).setTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
			((PDFGraphics2D) pdfGrapthics).closePage();
		} catch (IOException e) {
			((PDFGraphics2D) pdfGrapthics).endExport();
			throw new RuntimeException(e);
		}
	}

	private void closePDFGraphics(Graphics2D pdfGrapthics) {
		((PDFGraphics2D) pdfGrapthics).endExport();
	}

	private Dimension getPageDimension(UserProperties props) {
		Dimension d = PageConstants.getSize(props.getProperty(PDFGraphics2D.PAGE_SIZE), props.getProperty(PDFGraphics2D.ORIENTATION));
		Insets m = PageConstants.getMargins(props.getPropertyInsets(PDFGraphics2D.PAGE_MARGINS), props.getProperty(PDFGraphics2D.ORIENTATION));
		Dimension result = new Dimension();
		result.setSize(d.getWidth() - m.left - m.right, d.getHeight() - m.top - m.bottom);
		return result;
	}

	private UserProperties createPdfProperties(PageFormat format) {
		UserProperties props = new UserProperties();
		String orientation = format.getOrientation() == PageFormat.LANDSCAPE ? PageConstants.LANDSCAPE : PageConstants.PORTRAIT;
		String size = resolveSize(format);
		props.setProperty(PDFGraphics2D.ORIENTATION, orientation);
		props.setProperty(PDFGraphics2D.PAGE_SIZE, size);
		props.setProperty(PDFGraphics2D.FIT_TO_PAGE, false);
		props.setProperty(PDFGraphics2D.PAGE_MARGINS, "0, 0, 0, 0");
		props.setProperty(PDFGraphics2D.AUTHOR, "Webswing.org");
		props.setProperty(PDFGraphics2D.KEYWORDS, "Page:" + size + " Orientation:" + orientation );
		return props;
	}

	private String resolveSize(PageFormat format) {
		Paper p = format.getPaper();
		MediaSizeName size = WebPrinterJobWrapper.findMedia(p.getWidth(), p.getHeight());
		if (size == MediaSizeName.ISO_A3) {
			return PageConstants.A3;
		} else if (size == MediaSizeName.ISO_A4) {
			return PageConstants.A4;
		} else if (size == MediaSizeName.ISO_A5) {
			return PageConstants.A5;
		} else if (size == MediaSizeName.ISO_A6) {
			return PageConstants.A6;
		} else if (size == MediaSizeName.NA_LETTER) {
			return PageConstants.LETTER;
		} else if (size == MediaSizeName.NA_LEGAL) {
			return PageConstants.LEGAL;
		} else if (size == MediaSizeName.EXECUTIVE) {
			return PageConstants.EXECUTIVE;
		} else if (size == MediaSizeName.LEDGER) {
			return PageConstants.LEDGER;
		} else {
			return PageConstants.A4;
		}
	}

	private boolean isInRange(int i, PrintRequestAttributeSet attribs) {
		PageRanges range = null;
		if ((range = (PageRanges) attribs.get(PageRanges.class)) != null) {
			if (range.contains(i + 1)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
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
