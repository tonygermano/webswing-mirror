package sk.viktor.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import sk.viktor.ignored.PaintManager;

public class SwingDrawingServerHandler extends SimpleChannelUpstreamHandler {

    String urlMapping;

    public SwingDrawingServerHandler(String urlMapping) {
        this.urlMapping = urlMapping + "/";
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpRequest request = (HttpRequest) e.getMessage();
        if (request.getUri().startsWith(urlMapping)) {
            synchronized (PaintManager.bufferMap) {
                if (PaintManager.bufferMap.containsKey(request.getUri().substring(urlMapping.length()))) {
                    ChannelBuffer image = PaintManager.bufferMap.get(request.getUri().substring(urlMapping.length()));
                    writeResponse(image, e);
                }
            }
        } else {
            ctx.sendUpstream(e);
        }
    }

    private void writeResponse(ChannelBuffer image, MessageEvent e) {
        ChannelBuffer buf = image;
        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setContent(buf);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png");
        response.setHeader(HttpHeaders.Names.CONNECTION, "keep-alive");
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buf.readableBytes()));
        ChannelFuture future = e.getChannel().write(response);
    }
}
