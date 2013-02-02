package sk.viktor.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import com.corundumstudio.socketio.listener.DisconnectListener;

public class SwingServer {

    public static final String PAINT_ACK_PREFIX = "paintAck";
    public static final String UNLOAD_PREFIX = "unload";
    
    public static final String SWING_SHUTDOWN_NOTIFICATION = "shutDownNotification";
    public static final String TOO_MANY_CLIENTS_NOTIFICATION = "tooManyClientsNotification";
    public static final String SWING_KILL_SIGNAL = "killSwing";

    public static final String SWING_START_SYS_PROP_CLIENT_ID = "webswing.clientId";
    public static final String SWING_START_SYS_PROP_MAIN_CLASS = "webswing.mainClass";

    private static Connection connection;
    private static ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor();
    
    private static Map<String, SwingJvmConnection> swingInstanceMap = new ConcurrentHashMap<String, SwingJvmConnection>();

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

    public static void removeSwingClientApplication(String clientId) {
        swingInstanceMap.remove(clientId);
    }

    public static void startServer() throws Exception {
        Configuration config = new Configuration();
        config.setHostname(sk.viktor.Configuration.getInstance().getHost());
        config.setPort(Integer.valueOf(sk.viktor.Configuration.getInstance().getPort()));
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
                if(swingInstanceMap.size()>=sk.viktor.Configuration.getInstance().getClients()){
                    System.out.println("too many connections:" +swingInstanceMap.size());
                    client.sendMessage(TOO_MANY_CLIENTS_NOTIFICATION);
                    client.disconnect();
                    return;
                }
                System.out.println("connected to " + handshake.clientId);

                if (!swingInstanceMap.containsKey(handshake.clientId)) {
                    SwingJvmConnection appl = new SwingJvmConnection(handshake.clientId, connection, client);
                    swingInstanceMap.put(handshake.clientId, appl);
                    appl.start();
                } else {
                    SwingJvmConnection appl = swingInstanceMap.get(handshake.clientId);
                    if(appl.getExitSchedule()!=null){
                        appl.getExitSchedule().cancel(false);
                        appl.setExitSchedule(null);
                    }
                    appl.setClient(client);
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
                } else if (msg.startsWith(UNLOAD_PREFIX)) {
                    String clientId = msg.substring(UNLOAD_PREFIX.length());
                    swingInstanceMap.get(clientId).sendKill();
                }
            }
        });

        server.addDisconnectListener(new DisconnectListener() {

            public void onDisconnect(SocketIOClient client) {
                SwingJvmConnection current = null;
                for (SwingJvmConnection c : swingInstanceMap.values()) {
                    if (c.getClient() == client) {
                        current = c;
                    }
                }
                if (current != null) {
                    if(current.getExitSchedule()!=null){
                        current.getExitSchedule().cancel(false);
                        current.setExitSchedule(null);
                    }
                    ScheduledFuture<?> schedule = scheduler.schedule(current, 30, TimeUnit.SECONDS);
                    current.setExitTimer(schedule);
                    System.out.println("disconected");
                }

            }
        });

        server.start();
    }
}
