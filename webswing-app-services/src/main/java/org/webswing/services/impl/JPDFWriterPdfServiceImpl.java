package org.webswing.services.impl;

import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.OutputStream;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PageRanges;

import org.webswing.ext.services.PdfService;
import org.webswing.toolkit.WebPrinterJobWrapper;
import org.webswing.toolkit.util.DummyGraphics2D;

import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFPage;

public class JPDFWriterPdfServiceImpl implements PdfService {

	private static JPDFWriterPdfServiceImpl impl;
	private static Graphics2D dummyG = new DummyGraphics2D();

	public static JPDFWriterPdfServiceImpl getInstance() {
		if (impl == null) {
			impl = new JPDFWriterPdfServiceImpl();
		}
		return impl;
	}
	
	@Override
	public void printToPDF(OutputStream out, Pageable pageable, Printable printable, PrintRequestAttributeSet attribs) throws PrinterException, IOException {
		PDFDocument pdfDoc = new PDFDocument();
		PageFormat pageFormat = WebPrinterJobWrapper.toPageFormat(attribs);
		
		if (printable != null) {
			int i = 0;
			while (paintPdf(pdfDoc, pageFormat, attribs, printable, i++) != Printable.NO_SUCH_PAGE) {
			}
		} else if (pageable != null) {
			int no = pageable.getNumberOfPages();
			for (int i = 0; i < no; i++) {
				PageFormat pageablePageFormat = pageable.getPageFormat(i);
				paintPdf(pdfDoc, pageablePageFormat != null ? pageablePageFormat : pageFormat, attribs, pageable.getPrintable(i), i);
			}
		}
		
		pdfDoc.saveDocument(out);
	}

	private int paintPdf(PDFDocument pdfDoc, PageFormat pageFormat, PrintRequestAttributeSet attribs, Printable printable, int i) throws PrinterException {
		if (isInRange(i, attribs)) {
			if (printable != null && printable.print(dummyG, pageFormat, i) != Printable.NO_SUCH_PAGE) {
				PDFPage page = pdfDoc.createPage(pageFormat);
				int result = printable.print(page.createGraphics(), pageFormat, i);
				if (result == Printable.PAGE_EXISTS) {
					pdfDoc.addPage(page);
				}
				return result;
			}
			return Printable.NO_SUCH_PAGE;
		}
		return printable.print(dummyG, pageFormat, i);
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
	
}
