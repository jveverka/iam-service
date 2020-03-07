package itx.iamservice.model.extensions.authentication.up;

import itx.iamservice.model.AuthenticationRequest;
import itx.iamservice.model.ClientId;

public class UPAuthenticationRequest implements AuthenticationRequest<UPCredentialsType> {

    private final ClientId clientId;
    private final String password;

    public UPAuthenticationRequest(ClientId clientId, String password) {
        this.clientId = clientId;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public ClientId getClientId() {
        return getClientId();
    }

    @Override
    public UPCredentialsType getCredentialsType() {
        return new UPCredentialsType();
    }

}
