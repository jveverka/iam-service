package itx.iamservice.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.RoleId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TokenRequest {

    @JsonProperty("grant_type")
    private final GrantType grantType;

    private final String username;
    private final String password;
    private final String scope;

    @JsonIgnore
    private final Set<RoleId> scopes;

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
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        if (scope == null) {
            this.scope = "";
            this.scopes = Collections.emptySet();
        } else {
            this.scope = scope;
            this.scopes = new HashSet<>();
            String[] rawScopes = scope.trim().split(" ");
            for (String s: rawScopes) {
                if (!s.isEmpty()) {
                    scopes.add(RoleId.from(s));
                }
            }
        }
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

    @JsonIgnore
    public Set<RoleId> getScopes() {
        return scopes;
    }

}
