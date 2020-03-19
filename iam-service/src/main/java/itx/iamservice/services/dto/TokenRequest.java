package itx.iamservice.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRequest {

    @JsonProperty("grant_type")
    private final GrantType grantType;

    private final String username;
    private final String password;
    private final String scope;

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("client_secret")
    private final String clientSecret;

    @JsonCreator
    public TokenRequest(@JsonProperty("grant_type") String grantType,
                        @JsonProperty("username") String username,
                        @JsonProperty("password") String password,
                        @JsonProperty("scope") String scope,
                        @JsonProperty("client_id") String clientId,
                        @JsonProperty("client_secret") String clientSecret) {
        this.grantType = GrantType.getGrantType(grantType);
        this.username = username;
        this.password = password;
        this.scope = scope;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @JsonProperty("grant_type")
    public String getGrantType() {
        return grantType.getTypeName();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getScope() {
        return scope;
    }

    @JsonProperty("client_id")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty("client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    @JsonIgnore
    public GrantType getGrantTypeEnum() {
        return grantType;
    }

}
