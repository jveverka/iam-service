package itx.iamservice.core.model.extensions.authentication.up;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.RoleId;

import java.util.Set;

public class UPAuthenticationRequest implements AuthenticationRequest<UPCredentials> {

    private final UserId userId;
    private final String password;
    private final Set<RoleId> scope;
    private final ClientCredentials clientCredentials;

    public UPAuthenticationRequest(UserId userId, String password, Set<RoleId> scope, ClientCredentials clientCredentials) {
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
    public Set<RoleId> getScope() {
        return scope;
    }

    @Override
    public ClientCredentials getClientCredentials() {
        return clientCredentials;
    }

}
