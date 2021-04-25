package one.microproject.iamservice.persistence.mongo;

public class MongoConfiguration {

    private final String hostname;
    private final Integer port;
    private final String database;
    private final String username;
    private final String password;

    public MongoConfiguration(String hostname, Integer port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public MongoConfiguration(String hostname, Integer port, String database) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = null;
        this.password = null;
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConnectionString() {
        String usernameAndPassword = "";
        if (username != null) {
            usernameAndPassword = username + ":" + password + "@";
        }
        return "mongodb://" + usernameAndPassword + hostname + ":" + port + "/" + database;
    }

}
