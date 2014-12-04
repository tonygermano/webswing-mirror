package org.webswing.toolkit;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import javax.print.PrintService;

import org.webswing.Constants;
import org.webswing.model.s2c.PrinterJobResult;
import org.webswing.util.Services;
import org.webswing.util.Util;

public class WebPrinterJob extends PrinterJob {

    private Printable printable;
    private Pageable pageable;
    private PageFormat pageFormat;
    private int copies;
    private String jobName;
    private PrintService service = new WebPrintService();

    @Override
    public void setPrintable(Printable painter) {
        this.printable = painter;
    }

    @Override
    public void setPrintable(Printable painter, PageFormat format) {
        setPrintable(painter);
        this.pageFormat = format;
    }

    @Override
    public void setPageable(Pageable document) throws NullPointerException {
        this.pageable = document;
    }

    @Override
    public boolean printDialog() throws HeadlessException {
        return true;
    }

    @Override
    public PageFormat pageDialog(PageFormat page) throws HeadlessException {
        return page;
    }

    @Override
    public PageFormat defaultPage(PageFormat page) {
        return page;
    }

    @Override
    public PageFormat validatePage(PageFormat page) {
        return page;
    }

    @Override
    public void print() throws PrinterException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Graphics2D resultPdf = Services.getPdfService().createPDFGraphics(out, new Dimension(1, 1));
        if (printable != null) {
            int i = 0;
            boolean tryNext = true;
            while (tryNext) {
                int result = paintPfd(resultPdf, this.pageFormat, printable, i);
                if (result == Printable.NO_SUCH_PAGE) {
                    tryNext = false;
                } else {
                    i++;
                }
            }
        } else if (pageable != null) {
            int no = this.pageable.getNumberOfPages();
            for (int i = 0; i < no; i++) {
                paintPfd(resultPdf, this.pageable.getPageFormat(i), this.pageable.getPrintable(i), i);
            }
        }
        Services.getPdfService().closePDFGraphics(resultPdf);
        PrinterJobResult printResult = new PrinterJobResult();
        printResult.setClientId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
        printResult.setId(UUID.randomUUID().toString());
        printResult.setPdf(out.toByteArray());
        if (printResult.getPdf().length < 80000000) {
            Util.getWebToolkit().getPaintDispatcher().sendJsonObject(printResult);
        } else {
            System.err.println("Document size too large. size=" + printResult.getPdf().length);
        }
    }

    private int paintPfd(Graphics2D resultPdf, PageFormat pageFormat2, Printable printable2, int i) throws PrinterException {
        pageFormat2 = pageFormat2 == null ? defaultPage() : pageFormat2;
        int width = (int) pageFormat2.getPaper().getWidth();
        int height = (int) pageFormat2.getPaper().getHeight();
        if (printable2 != null) {
            Services.getPdfService().startPagePDFGraphics(resultPdf, new Dimension(width, height));
            double[] m = pageFormat2.getMatrix();
            resultPdf.setTransform(new AffineTransform(m[0], m[1], m[2], m[3], m[4], m[5]));
            int result = printable2.print(resultPdf, pageFormat2, i);
            Services.getPdfService().endPagePDFGraphics(resultPdf);
            return result;
        }
        return Printable.NO_SUCH_PAGE;
    }

    @Override
    public void setCopies(int copies) {
        this.copies = copies;
    }

    @Override
    public int getCopies() {
        return copies;
    }

    @Override
    public String getUserName() {
        return "";
    }

    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;

    }

    @Override
    public String getJobName() {
        return jobName;
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

}
