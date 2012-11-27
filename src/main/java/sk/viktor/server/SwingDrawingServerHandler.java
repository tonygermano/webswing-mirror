package sk.viktor.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import sk.viktor.ignored.common.PaintManager;
import sk.viktor.ignored.common.WebWindow;

public class SwingDrawingServerHandler extends SimpleChannelUpstreamHandler {

    String urlMapping;

    public SwingDrawingServerHandler(String urlMapping) {
        this.urlMapping = urlMapping + "/";
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) e.getMessage();
            if (request.getUri().startsWith(urlMapping)) {
                String[] requestStrings = request.getUri().substring(urlMapping.length()).split("/");
                String clientId = requestStrings[0];
                String windowGuid = requestStrings[1];
                WebWindow window = PaintManager.getInstance(clientId).getWebWindow(windowGuid);
                final ChannelBuffer image = ChannelBuffers.wrappedBuffer(window.getDiffWebData());
                HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                response.setContent(image);
                response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png");
                response.setHeader(HttpHeaders.Names.CONNECTION, "keep-alive");
                response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(image.readableBytes()));
                ChannelFuture future = e.getChannel().write(response);
                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture arg0) throws Exception {
                        image.discardReadBytes();
                    }
                });
            } else {
                ctx.sendUpstream(e);
            }
        } else {
            ctx.sendUpstream(e);
        }
    }
}
