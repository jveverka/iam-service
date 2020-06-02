package itx.iamservice.core.dto;

import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.TokenType;

public class IntrospectRequest {

    private final JWToken token;
    private final TokenType typeHint;

    public IntrospectRequest(JWToken token, TokenType typeHint) {
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
