package itx.iamservice.model;

public interface Credentials<CT extends CredentialsType, AR extends AuthenticationRequest> {

    ClientId getClientId();

    CT getType();

    boolean verify(AR authenticationRequest);

}
