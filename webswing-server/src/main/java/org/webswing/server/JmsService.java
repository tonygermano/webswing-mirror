package org.webswing.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.ConstantPendingMessageLimitStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.usage.SystemUsage;
import org.webswing.Constants;

@WebListener
public class JmsService implements ServletContextListener {

    private BrokerService broker;

    public void contextInitialized(ServletContextEvent event) {
        try {
            broker = startService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        try {
            broker.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public BrokerService startService() throws Exception {
        // BrokerService broker= BrokerFactory.createBroker("xbean:mq.xml");

        BrokerService broker = new BrokerService();
        broker.setUseJmx(false);
        broker.setPersistent(false);

        PolicyMap policyMap = new PolicyMap();
        PolicyEntry defaultEntry = new PolicyEntry();
        ConstantPendingMessageLimitStrategy pendingMessageLimitStrategy = new ConstantPendingMessageLimitStrategy();
        pendingMessageLimitStrategy.setLimit(10);
        defaultEntry.setPendingMessageLimitStrategy(pendingMessageLimitStrategy);
        policyMap.setDefaultEntry(defaultEntry);
        broker.setDestinationPolicy(policyMap);

        SystemUsage memoryManager = new SystemUsage();
        MemoryUsage memoryLimit = new MemoryUsage();
        memoryLimit.setLimit(20000000);

        memoryManager.setMemoryUsage(memoryLimit);
        broker.setSystemUsage(memoryManager);
        // configure the broker
        broker.addConnector(Constants.JMS_URL);

        broker.start();
        return broker;
                
    }
}

    
