package sk.viktor.server;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jboss.netty.channel.ChannelPipeline;

import sk.viktor.ignored.model.c2s.JsonConnectionHandshake;
import sk.viktor.ignored.model.c2s.JsonEventKeyboard;
import sk.viktor.ignored.model.c2s.JsonEventMouse;
import sk.viktor.ignored.model.c2s.JsonEventWindow;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOPipelineFactory;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class SwingServer {

    public  static final String PAINT_ACK_PREFIX = "paintAck";
    public  static final String SWING_SHUTDOWN_NOTIFICATION = "shutDownNotification";

    private static Connection connection;

    private static Map<String, WebSwingApplication> swingInstanceMap = new HashMap<String, WebSwingApplication>();

    static {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        try {
            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void removeSwingClientApplication(String clientId){
        swingInstanceMap.remove(clientId);
    }
    
    public static void startServer() throws Exception {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(7070);
        SocketIOServer server = new SocketIOServer(config);

        server.setPipelineFactory(new SocketIOPipelineFactory() {

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipe = super.getPipeline();
                pipe.addBefore(PACKET_HANDLER, "localResourceHandler", new ResourcesServerHandler("/"));
                //pipe.addBefore(PACKET_HANDLER, "swingHandler", new SwingDrawingServerHandler("/swing"));
                return pipe;
            }
        });

        server.addJsonObjectListener(JsonConnectionHandshake.class, new DataListener<JsonConnectionHandshake>() {

            public void onData(SocketIOClient client, JsonConnectionHandshake handshake, AckRequest paramAckRequest) {
                if (!swingInstanceMap.containsKey(handshake.clientId)) {
                    WebSwingApplication appl = new WebSwingApplication(handshake.clientId, connection, client);
                    swingInstanceMap.put(handshake.clientId, appl);
                    appl.start();
                }
            }
        });

        server.addJsonObjectListener(JsonEventMouse.class, new DataListener<JsonEventMouse>() {

            public void onData(SocketIOClient arg0, JsonEventMouse mouseEvt, AckRequest arg2) {
                swingInstanceMap.get(mouseEvt.clientId).send(mouseEvt);
            }
        });

        server.addJsonObjectListener(JsonEventWindow.class, new DataListener<JsonEventWindow>() {

            public void onData(SocketIOClient arg0, JsonEventWindow windowEvt, AckRequest arg2) {
                swingInstanceMap.get(windowEvt.clientId).send(windowEvt);
            }
        });

        server.addJsonObjectListener(JsonEventKeyboard.class, new DataListener<JsonEventKeyboard>() {

            public void onData(SocketIOClient arg0, JsonEventKeyboard kbdEvt, AckRequest arg2) {
                swingInstanceMap.get(kbdEvt.clientId).send(kbdEvt);
            }
        });

        server.addMessageListener(new DataListener<String>() {

            public void onData(SocketIOClient arg0, String msg, AckRequest arg2) {
                if (msg.startsWith(PAINT_ACK_PREFIX)) {
                    String clientId = msg.substring(PAINT_ACK_PREFIX.length());
                    swingInstanceMap.get(clientId).sendMsg(PAINT_ACK_PREFIX);
                }
            }
        });
        server.start();
    }
}
