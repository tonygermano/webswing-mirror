package sk.viktor;

public abstract class Configuration {

    private static Configuration singleton = new ConfigurationImpl();

    public abstract String getHost();

    public abstract String getPort();

    public abstract String getArgs();

    public abstract String getVmargs();

    public abstract String getMain();

    public abstract int getClients();

    public static Configuration getInstance() {
        return singleton;
    }

}
