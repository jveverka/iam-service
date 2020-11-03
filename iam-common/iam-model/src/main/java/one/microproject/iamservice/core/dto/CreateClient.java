package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.ClientProperties;

public class CreateClient {

    private final String id;
    private final String name;
    private final Long defaultAccessTokenDuration;
    private final Long defaultRefreshTokenDuration;
    private final String secret;
    private final ClientProperties properties;

    @JsonCreator
    public CreateClient(@JsonProperty("id") String id,
                        @JsonProperty("name") String name,
                        @JsonProperty("defaultAccessTokenDuration") Long defaultAccessTokenDuration,
                        @JsonProperty("defaultRefreshTokenDuration") Long defaultRefreshTokenDuration,
                        @JsonProperty("secret") String secret,
                        @JsonProperty("properties") ClientProperties properties) {
        this.id = id;
        this.name = name;
        this.defaultAccessTokenDuration = defaultAccessTokenDuration;
        this.defaultRefreshTokenDuration = defaultRefreshTokenDuration;
        this.secret = secret;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getDefaultAccessTokenDuration() {
        return defaultAccessTokenDuration;
    }

    public Long getDefaultRefreshTokenDuration() {
        return defaultRefreshTokenDuration;
    }

    public String getSecret() {
        return secret;
    }

    public ClientProperties getProperties() {
        return properties;
    }

}
