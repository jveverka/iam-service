package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.ClientId;

public class CreateClientRequest {

    private final ClientId id;
    private final String name;
    private final Long defaultAccessTokenDuration;
    private final Long defaultRefreshTokenDuration;

    @JsonCreator
    public CreateClientRequest(@JsonProperty("id") ClientId id,
                               @JsonProperty("name") String name,
                               @JsonProperty("defaultAccessTokenDuration") Long defaultAccessTokenDuration,
                               @JsonProperty("defaultRefreshTokenDuration") Long defaultRefreshTokenDuration) {
        this.id = id;
        this.name = name;
        this.defaultAccessTokenDuration = defaultAccessTokenDuration;
        this.defaultRefreshTokenDuration = defaultRefreshTokenDuration;
    }

    public ClientId getId() {
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

}
