package one.microproject.iamservice.core.model.extensions.authentication.up;

import one.microproject.iamservice.core.model.AuthenticationRequest;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.dto.Scope;


public class UPAuthenticationRequest implements AuthenticationRequest<UPCredentials> {

    private final UserId userId;
    private final String password;
    private final Scope scope;
    private final ClientCredentials clientCredentials;

    public UPAuthenticationRequest(UserId userId, String password, Scope scope, ClientCredentials clientCredentials) {
        this.userId = userId;
        this.password = password;
        this.scope = scope;
        this.clientCredentials = clientCredentials;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public UserId getUserId() {
        return userId;
    }

    @Override
    public Class<UPCredentials> getCredentialsType() {
        return UPCredentials.class;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public ClientCredentials getClientCredentials() {
        return clientCredentials;
    }

}
