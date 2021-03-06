package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.PKCEMethod;

import java.util.Collection;

public class AuthorizationCodeGrantRequest {

    private final String username;
    private final String password;
    private final String clientId;
    private final Collection<String> scopes;
    private final String state;
    private final String redirectUri;
    private final String codeChallenge;
    private final PKCEMethod codeChallengeMethod;

    @JsonCreator
    public AuthorizationCodeGrantRequest(@JsonProperty("username") String username,
                                         @JsonProperty("password") String password,
                                         @JsonProperty("clientId") String clientId,
                                         @JsonProperty("scopes") Collection<String> scopes,
                                         @JsonProperty("state") String state,
                                         @JsonProperty("redirectUri") String redirectUri,
                                         @JsonProperty("codeChallenge") String codeChallenge,
                                         @JsonProperty("codeChallengeMethod") PKCEMethod codeChallengeMethod) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.scopes = scopes;
        this.state = state;
        this.redirectUri = redirectUri;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
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

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public PKCEMethod getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

}
