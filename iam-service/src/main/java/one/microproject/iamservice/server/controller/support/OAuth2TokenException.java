package one.microproject.iamservice.server.controller.support;

import one.microproject.iamservice.core.dto.TokenResponseError;

public class OAuth2TokenException extends Exception {

    private final TokenResponseError tokenResponseError;

    public OAuth2TokenException(TokenResponseError tokenResponseError) {
        super();
        this.tokenResponseError = tokenResponseError;
    }

    public OAuth2TokenException(Throwable t, TokenResponseError tokenResponseError) {
        super(t);
        this.tokenResponseError = tokenResponseError;
    }

    public TokenResponseError getTokenResponseError() {
        return tokenResponseError;
    }

}
