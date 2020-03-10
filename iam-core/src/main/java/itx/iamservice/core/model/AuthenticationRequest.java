package itx.iamservice.core.model;

public interface AuthenticationRequest<C extends CredentialsType> {

    ClientId getClientId();

    C getCredentialsType();

}
