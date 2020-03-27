package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateUserRequest {

    private final String name;
    private final Long defaultAccessTokenDuration;
    private final Long defaultRefreshTokenDuration;

    @JsonCreator
    public CreateUserRequest(@JsonProperty("name") String name,
                             @JsonProperty("defaultAccessTokenDuration") Long defaultAccessTokenDuration,
                             @JsonProperty("defaultRefreshTokenDuration") Long defaultRefreshTokenDuration) {
        this.name = name;
        this.defaultAccessTokenDuration = defaultAccessTokenDuration;
        this.defaultRefreshTokenDuration = defaultRefreshTokenDuration;
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

}
