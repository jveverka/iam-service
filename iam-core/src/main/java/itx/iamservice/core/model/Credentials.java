package itx.iamservice.core.model;

public interface Credentials<CT extends CredentialsType, AR extends AuthenticationRequest> {

    ClientId getClientId();

    CT getType();

    boolean verify(AR authenticationRequest);

}
