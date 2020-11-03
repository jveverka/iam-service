package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ClientProperties {

    private final String redirectURL;
    private final Boolean authorizationCodeGrantEnabled;
    private final Boolean passwordCredentialsEnabled;
    private final Boolean clientCredentialsEnabled;
    private final Map<String, String> properties;

    @JsonCreator
    public ClientProperties(@JsonProperty("redirectURL") String redirectURL,
                            @JsonProperty("authorizationCodeGrantEnabled") Boolean authorizationCodeGrantEnabled,
                            @JsonProperty("passwordCredentialsEnabled") Boolean passwordCredentialsEnabled,
                            @JsonProperty("clientCredentialsEnabled") Boolean clientCredentialsEnabled,
                            @JsonProperty("properties") Map<String, String> properties) {
        this.redirectURL = redirectURL;
        this.authorizationCodeGrantEnabled = authorizationCodeGrantEnabled;
        this.passwordCredentialsEnabled = passwordCredentialsEnabled;
        this.clientCredentialsEnabled = clientCredentialsEnabled;
        this.properties = properties;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public Boolean getAuthorizationCodeGrantEnabled() {
        return authorizationCodeGrantEnabled;
    }

    public Boolean getPasswordCredentialsEnabled() {
        return passwordCredentialsEnabled;
    }

    public Boolean getClientCredentialsEnabled() {
        return clientCredentialsEnabled;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public static ClientProperties from(String redirectURL) {
        return new ClientProperties(redirectURL, true, true, true, new HashMap<>());
    }

}
