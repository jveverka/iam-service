package itx.iamservice.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {

    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("expires_in")
    private final Long expiresIn;

    @JsonProperty("refresh_expires_in")
    private final Long refreshExpiresIn;

    @JsonProperty("refresh_token")
    private final String refreshToken;

    @JsonProperty("token_type")
    private final String tokenType;

    @JsonCreator
    public TokenResponse(@JsonProperty("access_token") String accessToken,
                         @JsonProperty("expires_in") Long expiresIn,
                         @JsonProperty("refresh_expires_in") Long refreshExpiresIn,
                         @JsonProperty("refresh_token") String refreshToken,
                         @JsonProperty("token_type") String tokenType) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("expires_in")
    public Long getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("refresh_expires_in")
    public Long getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    @JsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }
}
