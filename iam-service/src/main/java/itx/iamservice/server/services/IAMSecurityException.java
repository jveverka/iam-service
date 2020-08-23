package itx.iamservice.server.services;

public class IAMSecurityException extends RuntimeException {

    public IAMSecurityException(String message) {
        super(message);
    }

    public IAMSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public IAMSecurityException(Throwable cause) {
        super(cause);
    }

}
