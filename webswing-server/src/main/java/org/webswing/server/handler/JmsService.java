package org.webswing.server.handler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.ConstantPendingMessageLimitStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.usage.SystemUsage;
import org.webswing.Constants;

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
		broker.setUseJmx(true);
		broker.setPersistent(false);

		PolicyMap policyMap = new PolicyMap();
		PolicyEntry defaultEntry = new PolicyEntry();

		ConstantPendingMessageLimitStrategy pendingMessageLimitStrategy = new ConstantPendingMessageLimitStrategy();
		pendingMessageLimitStrategy.setLimit(10);
		defaultEntry.setPendingMessageLimitStrategy(pendingMessageLimitStrategy);
		defaultEntry.setMemoryLimit(5 * 1024 * 1024);

		policyMap.setDefaultEntry(defaultEntry);
		broker.setDestinationPolicy(policyMap);

		SystemUsage memoryManager = new SystemUsage();
		MemoryUsage memoryLimit = new MemoryUsage();
		memoryLimit.setLimit(80 * 1024 * 1024);

		memoryManager.setMemoryUsage(memoryLimit);
		broker.setSystemUsage(memoryManager);
		// configure the broker
		broker.addConnector(getUrl());

		broker.start();
		return broker;

	}

	public static String getUrl() {
		return System.getProperty(Constants.JMS_URL, Constants.JMS_URL_DEFAULT);
	}
}
