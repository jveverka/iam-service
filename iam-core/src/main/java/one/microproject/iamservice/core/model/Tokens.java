package one.microproject.iamservice.core.model;

public class Tokens {

    private final JWToken accessToken;
    private final JWToken refreshToken;
    private final TokenType tokenType;
    private final Long expiresIn;
    private final Long refreshExpiresIn;
    private final JWToken idToken;

    public Tokens(JWToken accessToken, JWToken refreshToken, TokenType tokenType,
                  Long expiresIn, Long refreshExpiresIn, JWToken idToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
        this.idToken = idToken;
    }

    public JWToken getAccessToken() {
        return accessToken;
    }

    public JWToken getRefreshToken() {
        return refreshToken;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public Long getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public JWToken getIdToken() {
        return idToken;
    }
}
