package itx.iamservice.model;

public interface AuthenticationRequest<CT extends CredentialsType> {

    ClientId getClientId();

    CT getCredentialsType();

}
