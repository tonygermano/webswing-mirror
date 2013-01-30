package sk.viktor;

import sk.viktor.server.JmsService;
import sk.viktor.server.SwingServer;

public class Main {


    public static void main(String[] args) throws Exception {
        JmsService.startService();
        SwingServer.startServer();

    }

}
