package org.webswing.debug.tool;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.ConstantPendingMessageLimitStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.usage.SystemUsage;
import org.webswing.Constants;
import org.webswing.debug.tool.ui.DebugFrame;
import org.webswing.server.JmsService;


public class Main {
   
    private static Connection connection;

    static {
        try {
            JmsService.startService();
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.JMS_URL);
            // Create a Connection
            connection = connectionFactory.createConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void main(String[] args) throws Exception {
        DebugFrame df= new DebugFrame();
        SwingJvmConnection appl = new SwingJvmConnection("debug", connection,df);
    }
}
