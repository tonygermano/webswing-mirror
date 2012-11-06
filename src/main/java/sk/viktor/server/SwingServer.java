package sk.viktor.server;

import java.awt.event.MouseEvent;

import org.jboss.netty.channel.ChannelPipeline;

import sk.viktor.Main;
import sk.viktor.ignored.PaintManager;
import sk.viktor.ignored.event.model.JsonEventMouse;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOPipelineFactory;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;

public class SwingServer {

    public static void startServer() throws Exception {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8080);
        SocketIOServer server = new SocketIOServer(config);
        
        server.setPipelineFactory(new SocketIOPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipe= super.getPipeline();
                pipe.addBefore(PACKET_HANDLER, "localResourceHandler", new ResourcesServerHandler("/"));
                pipe.addBefore(PACKET_HANDLER, "swingHandler", new SwingDrawingServerHandler("/swing"));
                return pipe;
            }
        });
        server.addConnectListener(new ConnectListener() {
            
            public void onConnect(SocketIOClient client) {
                PaintManager.client=client;
                try {
                    Main.startSwing();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        server.addJsonObjectListener(JsonEventMouse.class,new DataListener<JsonEventMouse>() {

            public void onData(SocketIOClient arg0, JsonEventMouse mouseEvt, AckRequest arg2) {
                PaintManager.dispatchEvent(mouseEvt);
                System.out.println("sending " +mouseEvt);
            }
        });
        
        server.start();
    }
}
