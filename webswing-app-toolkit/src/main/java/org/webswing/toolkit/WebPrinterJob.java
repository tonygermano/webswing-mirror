package org.webswing.toolkit;

import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.UUID;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;

import org.webswing.Constants;
import org.webswing.model.internal.PrinterJobResultMsgInternal;
import org.webswing.toolkit.util.DummyGraphics2D;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Services;
import org.webswing.toolkit.util.Util;
import org.webswing.toolkit.util.WebPrintDialog;

public class WebPrinterJob extends PrinterJob {
	private static final int DPI = 72;

	private static Graphics2D dummyG = new DummyGraphics2D();

	private Printable printable;
	private Pageable pageable;
	private PrintRequestAttributeSet attribs = new HashPrintRequestAttributeSet();
	private PrintService service;

	public WebPrinterJob() {
		service = WebPrintService.getService();
		setPageFormat(defaultPage());
	}

	@Override
	public void setPrintable(Printable painter) {
		this.printable = painter;
	}

	@Override
	public void setPrintable(Printable painter, PageFormat format) {
		setPrintable(painter);
		setPageFormat(format);
	}

	@Override
	public void setPageable(Pageable document) throws NullPointerException {
		this.pageable = document;
	}

	@Override
	public PageFormat validatePage(PageFormat page) {
		PrintRequestAttributeSet a = toAttributes(page);
		PageFormat p = toPageFormat(a);
		return p;
	}

	private void setPageFormat(PageFormat defaultPage) {
		PrintRequestAttributeSet a = toAttributes(defaultPage);
		attribs.addAll(a);
	}

	@Override
	public PageFormat defaultPage(PageFormat page) {
		return defaultPage();
	}

	@Override
	public PageFormat defaultPage() {
		HashPrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
		attrs.add((OrientationRequested) service.getDefaultAttributeValue(OrientationRequested.class));
		attrs.add((MediaSizeName) service.getDefaultAttributeValue(Media.class));
		attrs.add((MediaPrintableArea) service.getDefaultAttributeValue(MediaPrintableArea.class));
		return toPageFormat(attrs);
	}

	private PageFormat toPageFormat(PrintRequestAttributeSet a) {
		OrientationRequested orientation = (OrientationRequested) a.get(OrientationRequested.class);
		MediaSizeName name = (MediaSizeName) a.get(Media.class);
		MediaPrintableArea area = (MediaPrintableArea) a.get(MediaPrintableArea.class);
		PageFormat result = new PageFormat();
		if (orientation != null) {
			if (orientation == OrientationRequested.LANDSCAPE) {
				result.setOrientation(PageFormat.LANDSCAPE);
			} else {
				result.setOrientation(PageFormat.PORTRAIT);
			}
		}
		Paper p = new Paper();
		if (name != null) {
			MediaSize msz = MediaSize.getMediaSizeForName(name);
			if (msz != null) {
				double paperWid = msz.getX(MediaSize.INCH) * DPI;
				double paperHgt = msz.getY(MediaSize.INCH) * DPI;
				p.setSize(paperWid, paperHgt);
				if (area == null) {
					p.setImageableArea(DPI, DPI, paperWid - 1 * DPI, paperHgt - 1 * DPI);
				}
			}
		}
		if (area != null) {
			float[] printableArea = area.getPrintableArea(MediaPrintableArea.INCH);
			for (int i = 0; i < printableArea.length; i++) {
				printableArea[i] = printableArea[i] * DPI;
			}
			p.setImageableArea(printableArea[0], printableArea[1], printableArea[2], printableArea[3]);
		}
		result.setPaper(p);
		return result;
	}

	private PrintRequestAttributeSet toAttributes(PageFormat format) {
		HashPrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
		OrientationRequested orientation = format.getOrientation() == PageFormat.LANDSCAPE ? OrientationRequested.LANDSCAPE : OrientationRequested.PORTRAIT;
		Paper p = format.getPaper();
		MediaSizeName size = findMedia(p.getWidth(), p.getHeight());
		float x = (float) p.getImageableY() / DPI;
		float y = (float) p.getImageableX() / DPI;
		float w = (float) p.getImageableWidth() / DPI;
		float h = (float) p.getImageableHeight() / DPI;
		MediaPrintableArea margins = new MediaPrintableArea(x, y, w, h, MediaPrintableArea.INCH);
		attrs.add(size);
		attrs.add(orientation);
		attrs.add(margins);
		return attrs;
	}

	public static MediaSizeName findMedia(double width, double height) {
		MediaSizeName[] mediaArray = WebPrintService.mediaSizes;
		MediaSizeName result = WebPrintService.mediaSizes[0];
		float w = (float) (width / DPI);
		float h = (float) (height / DPI);
		if ((w <= 0.0F) || (h <= 0.0F)) {
			throw new IllegalArgumentException("args must be +ve values");
		}

		double d1 = w * w + h * h;

		float f1 = w;
		float f2 = h;

		for (int i = 0; i < mediaArray.length; i++) {
			MediaSizeName mediaSizeName = (MediaSizeName) mediaArray[i];
			MediaSize mSize = MediaSize.getMediaSizeForName(mediaSizeName);
			float[] size = mSize.getSize(MediaSize.INCH);
			if ((w == size[0]) && (h == size[1])) {
				result = mediaSizeName;
				break;
			}
			f1 = w - size[0];
			f2 = h - size[1];
			double d2 = f1 * f1 + f2 * f2;
			if (d2 < d1) {
				d1 = d2;
				result = mediaSizeName;
			}
		}

		return result;
	}

	@Override
	public boolean printDialog() throws HeadlessException {
		WebPrintDialog dialog = new WebPrintDialog(service, attribs, null);
		dialog.setVisible(true);
		boolean confirmed = dialog.getStatus() == 1 ? true : false;
		if (confirmed) {
			PrintRequestAttributeSet a = dialog.getAttributes();
			attribs.addAll(a);
		}
		return confirmed;
	}

	public boolean printDialog(PrintRequestAttributeSet attributes) throws HeadlessException {
		if (attributes == null) {
			throw new NullPointerException("attributes");
		} else {
			this.attribs.addAll(attributes);
		}
		boolean result = printDialog();
		attributes.addAll(attribs);
		return result;
	}

	@Override
	public PageFormat pageDialog(PageFormat page) throws HeadlessException {
		setPageFormat(page);
		printDialog();
		return toPageFormat(attribs);
	}

	public PageFormat pageDialog(PrintRequestAttributeSet attributes) throws HeadlessException {

		if (attributes == null) {
			throw new NullPointerException("attributes");
		}
		this.attribs.addAll(attributes);
		printDialog();
		return toPageFormat(attribs);
	}

	@Override
	public void print(PrintRequestAttributeSet attributes) throws PrinterException {
		this.attribs.addAll(attributes);
		print();
	}

	@Override
	public void print() throws PrinterException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PageFormat pageFormat = toPageFormat(attribs);
		Graphics2D resultPdf = Services.getPdfService().createPDFGraphics(out, pageFormat);
		if (printable != null) {
			int i = 0;
			boolean tryNext = true;
			while (tryNext) {
				int result = paintPdf(resultPdf, pageFormat, printable, i);
				if (result == Printable.NO_SUCH_PAGE) {
					tryNext = false;
				} else {
					i++;
				}
			}
		} else if (pageable != null) {
			int no = this.pageable.getNumberOfPages();
			for (int i = 0; i < no; i++) {
				paintPdf(resultPdf, this.pageable.getPageFormat(i), this.pageable.getPrintable(i), i);
			}
		}
		Services.getPdfService().closePDFGraphics(resultPdf);
		String tempDir = System.getProperty(Constants.TEMP_DIR_PATH);
		String id = UUID.randomUUID().toString();
		File f = new File(URI.create(tempDir + "/" + id + ".pdf"));
		try {
			FileOutputStream output = new FileOutputStream(f);
			out.writeTo(output);
			output.close();
		} catch (Exception e) {
			Logger.error("Failed to save print pdf file to location " + f.getAbsolutePath(), e);
		}
		PrinterJobResultMsgInternal printResult = new PrinterJobResultMsgInternal();
		printResult.setClientId(id);
		printResult.setId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
		printResult.setPdfFile(f);
		Util.getWebToolkit().getPaintDispatcher().sendObject(printResult);
	}

	private int paintPdf(Graphics2D resultPdf, PageFormat pageFormat2, Printable printable2, int i) throws PrinterException {
		PageFormat pageFormat = toPageFormat(attribs);
		pageFormat2 = pageFormat2 == null ? pageFormat : pageFormat2;
		if (isInRange(i)) {
			if (printable2 != null && printable2.print(dummyG, pageFormat2, i) != Printable.NO_SUCH_PAGE) {
				Services.getPdfService().startPagePDFGraphics(resultPdf, pageFormat2);
				int result = printable2.print(resultPdf, pageFormat2, i);
				Services.getPdfService().endPagePDFGraphics(resultPdf);
				return result;
			}
			return Printable.NO_SUCH_PAGE;
		}
		return printable2.print(dummyG, pageFormat2, i);
	}

	private boolean isInRange(int i) {
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

	@Override
	public void setCopies(int copies) {
	}

	@Override
	public int getCopies() {
		return 1;
	}

	@Override
	public String getUserName() {
		return "";
	}

	@Override
	public void setJobName(String jobName) {
	}

	@Override
	public String getJobName() {
		return "";
	}

	@Override
	public void cancel() {
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public PrintService getPrintService() {
		return service;
	}

	@Override
	public void setPrintService(PrintService service) throws PrinterException {
	}

}
