package itx.iamservice.core.model.extensions.authentication.up;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Credentials;

public class UPCredentials implements Credentials<UPCredentialsType, UPAuthenticationRequest> {

    private final ClientId clientId;
    private final String password;

    public UPCredentials(ClientId clientId, String password) {
        this.clientId = clientId;
        this.password = password;
    }

    @Override
    public ClientId getClientId() {
        return null;
    }

    @Override
    public UPCredentialsType getType() {
        return new UPCredentialsType();
    }

    @Override
    public boolean verify(UPAuthenticationRequest authenticationRequest) {
        return clientId.equals(authenticationRequest.getClientId()) && password.equals(authenticationRequest.getPassword());
    }

}
