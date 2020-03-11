package itx.iamservice.core.model.extensions.authentication.up;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.RoleId;

import java.util.Set;

public class UPAuthenticationRequest implements AuthenticationRequest<UPCredentialsType> {

    private final ClientId clientId;
    private final String password;
    private final Set<RoleId> scope;

    public UPAuthenticationRequest(ClientId clientId, String password, Set<RoleId> scope) {
        this.clientId = clientId;
        this.password = password;
        this.scope = scope;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public ClientId getClientId() {
        return clientId;
    }

    @Override
    public UPCredentialsType getCredentialsType() {
        return new UPCredentialsType();
    }

    @Override
    public Set<RoleId> getScope() {
        return scope;
    }

}
