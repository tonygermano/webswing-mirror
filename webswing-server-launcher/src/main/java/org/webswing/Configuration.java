package org.webswing;

public abstract class Configuration {

    private static Configuration singleton = new ConfigurationImpl();

    public abstract String getHost();

    public abstract String getPort();

    public abstract String getConfigFile();
    
    public abstract String getUsersFile();

    public static Configuration getInstance() {
        return singleton;
    }

}
