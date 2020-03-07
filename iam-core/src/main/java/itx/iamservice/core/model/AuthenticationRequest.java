package itx.iamservice.core.model;

public interface AuthenticationRequest<CT extends CredentialsType> {

    ClientId getClientId();

    CT getCredentialsType();

}
