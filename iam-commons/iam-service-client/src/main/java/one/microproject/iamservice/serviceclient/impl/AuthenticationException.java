package one.microproject.iamservice.serviceclient.impl;

public class AuthenticationException extends Exception {

    public AuthenticationException(Throwable t) {
        super(t);
    }

    public AuthenticationException(String message) {
        super(message);
    }

}
