package org.webswing;

public abstract class Configuration {

    private static Configuration singleton = new ConfigurationImpl();

    public abstract String getHost();

    public abstract String getPort();

    public static Configuration getInstance() {
        return singleton;
    }

}
