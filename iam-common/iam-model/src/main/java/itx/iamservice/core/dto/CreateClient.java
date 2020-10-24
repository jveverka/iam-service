package itx.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateClient {

    private final String id;
    private final String name;
    private final Long defaultAccessTokenDuration;
    private final Long defaultRefreshTokenDuration;
    private final String secret;

    @JsonCreator
    public CreateClient(@JsonProperty("id") String id,
                        @JsonProperty("name") String name,
                        @JsonProperty("defaultAccessTokenDuration") Long defaultAccessTokenDuration,
                        @JsonProperty("defaultRefreshTokenDuration") Long defaultRefreshTokenDuration,
                        @JsonProperty("secret") String secret) {
        this.id = id;
        this.name = name;
        this.defaultAccessTokenDuration = defaultAccessTokenDuration;
        this.defaultRefreshTokenDuration = defaultRefreshTokenDuration;
        this.secret = secret;
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

}
