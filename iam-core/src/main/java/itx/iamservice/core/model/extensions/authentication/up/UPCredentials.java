package itx.iamservice.core.model.extensions.authentication.up;

import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.Credentials;

public class UPCredentials implements Credentials<UPCredentialsType, UPAuthenticationRequest> {

    private final UserId userId;
    private final String password;

    public UPCredentials(UserId userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public UserId getUserId() {
        return null;
    }

    @Override
    public UPCredentialsType getType() {
        return new UPCredentialsType();
    }

    @Override
    public boolean verify(UPAuthenticationRequest authenticationRequest) {
        return userId.equals(authenticationRequest.getUserId()) && password.equals(authenticationRequest.getPassword());
    }

}
