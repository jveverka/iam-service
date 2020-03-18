package itx.iamservice.core.model;

public interface Credentials<CT extends CredentialsType, AR extends AuthenticationRequest> {

    UserId getUserId();

    CT getType();

    boolean verify(AR authenticationRequest);

}
