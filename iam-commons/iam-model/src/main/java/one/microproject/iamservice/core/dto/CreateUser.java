package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.UserProperties;

public class CreateUser {

    private final String id;
    private final String name;
    private final Long defaultAccessTokenDuration;
    private final Long defaultRefreshTokenDuration;
    private final String email;
    private final String password;
    private final UserProperties userProperties;

    @JsonCreator
    public CreateUser(@JsonProperty("id") String id,
                      @JsonProperty("name") String name,
                      @JsonProperty("defaultAccessTokenDuration") Long defaultAccessTokenDuration,
                      @JsonProperty("defaultRefreshTokenDuration") Long defaultRefreshTokenDuration,
                      @JsonProperty("email") String email,
                      @JsonProperty("password") String password,
                      @JsonProperty("userProperties") UserProperties userProperties) {
        this.id = id;
        this.name = name;
        this.defaultAccessTokenDuration = defaultAccessTokenDuration;
        this.defaultRefreshTokenDuration = defaultRefreshTokenDuration;
        this.email = email;
        this.password = password;
        this.userProperties = userProperties;
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserProperties getUserProperties() {
        return userProperties;
    }

}
