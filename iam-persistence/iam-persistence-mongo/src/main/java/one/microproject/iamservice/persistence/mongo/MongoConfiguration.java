package one.microproject.iamservice.persistence.mongo;

public class MongoConfiguration {

    private final String hostname;
    private final Integer port;
    private final String database;

    public MongoConfiguration(String hostname, Integer port, String database) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getConnectionString() {
        return "mongodb://" + hostname + ":" + port;
    }

}
