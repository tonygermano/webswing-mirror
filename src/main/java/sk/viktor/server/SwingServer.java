package sk.viktor.server;

import org.jboss.netty.channel.ChannelPipeline;

import sk.viktor.Main;
import sk.viktor.ignored.PaintManager;

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
                pipe.addBefore(PACKET_HANDLER, "resourceHandler", new ResourcesServerHandler("/"));
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
        
        server.addMessageListener(new DataListener<String>() {
            
            public void onData(SocketIOClient arg0, String arg1) {
                System.out.println("message:"+arg1);
            }
        });
        
        server.start();
    }
}
