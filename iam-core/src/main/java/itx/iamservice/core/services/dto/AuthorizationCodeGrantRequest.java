package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class AuthorizationCodeGrantRequest {

    private final String username;
    private final String password;
    private final String clientId;
    private final Collection<String> scopes;
    private final String state;

    @JsonCreator
    public AuthorizationCodeGrantRequest(@JsonProperty("username") String username,
                                         @JsonProperty("password") String password,
                                         @JsonProperty("clientId") String clientId,
                                         @JsonProperty("scopes") Collection<String> scopes,
                                         @JsonProperty("state") String state) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.scopes = scopes;
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public Collection<String> getScopes() {
        return scopes;
    }

    public String getState() {
        return state;
    }

}
