package one.microproject.iamservice.core.services.dto;

import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.TokenType;

public class RevokeTokenRequest {

    private final JWToken token;
    private final TokenType typeHint;

    public RevokeTokenRequest(JWToken token, TokenType typeHint) {
        this.token = token;
        this.typeHint = typeHint;
    }

    public JWToken getToken() {
        return token;
    }

    public TokenType getTypeHint() {
        return typeHint;
    }

}
