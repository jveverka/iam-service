package one.microproject.iamservice.caches.redis;

public class RedisConfiguration {

    private final String hostname;
    private final Integer port;

    public RedisConfiguration(String hostname, Integer port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }

}
