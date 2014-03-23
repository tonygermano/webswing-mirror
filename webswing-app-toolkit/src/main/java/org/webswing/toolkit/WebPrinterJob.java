package org.webswing.toolkit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.webswing.Constants;
import org.webswing.model.s2c.PrinterJobResult;
import org.webswing.util.Util;

public class WebPrinterJob extends PrinterJob {

    private Printable printable;
    private Pageable pageable;
    private PageFormat pageFormat;
    private int copies;
    private String jobName;

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
        List<BufferedImage> bis=new ArrayList<BufferedImage>();
        if(printable!=null){
            int i=0;
            boolean tryNext=true;
            while(tryNext){
                BufferedImage img=createImage(this.pageFormat,printable,i);
                if(img==null){
                    tryNext=false;
                }else{
                    i++;
                    bis.add(img);
                }
            }
        }else if(pageable!=null){
            int no=this.pageable.getNumberOfPages();
            for(int i=0;i<no;i++){
                bis.add(createImage(this.pageFormat,printable,i));
            }
        }
        byte[] result = Util.getWebToolkit().getImageService().generatePDF(bis);
        PrinterJobResult printResult=new PrinterJobResult();
        printResult.setClientId(System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID));
        printResult.setId(UUID.randomUUID().toString());
        printResult.setPdf(result);
        Util.getWebToolkit().getPaintDispatcher().sendJsonObject(printResult);
    }

    private BufferedImage createImage(PageFormat pageFormat2, Printable printable2, int i) throws PrinterException {
        pageFormat2=pageFormat2==null?defaultPage():pageFormat2;
        int width= (int) pageFormat2.getPaper().getWidth();
        int height= (int) pageFormat2.getPaper().getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g= (Graphics2D) img.getGraphics();
        g.setBackground(Color.WHITE);
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, width, height);
        double[] m = pageFormat2.getMatrix();
        g.getTransform().setTransform(m[0], m[1], m[2], m[3], m[4], m[5]);
        g.setClip((int)pageFormat2.getImageableX(),(int) pageFormat2.getImageableY(),(int) pageFormat2.getImageableWidth(),(int) pageFormat2.getImageableHeight());
        if(printable2!=null){
            int result=printable2.print(g, pageFormat2, i);
            if(result== Printable.NO_SUCH_PAGE){
                g.dispose();
                return null;
            }
        }
        g.dispose();
        return img;
    }

    @Override
    public void setCopies(int copies) {
        this.copies=copies;
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
        this.jobName=jobName;

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

}
