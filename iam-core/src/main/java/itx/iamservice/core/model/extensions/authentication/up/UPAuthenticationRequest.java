package itx.iamservice.core.model.extensions.authentication.up;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.RoleId;

import java.util.Set;

public class UPAuthenticationRequest implements AuthenticationRequest<UPCredentialsType> {

    private final UserId userId;
    private final String password;
    private final Set<RoleId> scope;

    public UPAuthenticationRequest(UserId userId, String password, Set<RoleId> scope) {
        this.userId = userId;
        this.password = password;
        this.scope = scope;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public UserId getUserId() {
        return userId;
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
