package one.microproject.iamservice.core.dto;

public class TokenResponseWrapper {

    private final TokenResponse tokenResponse;
    private final TokenResponseError tokenResponseError;

    public TokenResponseWrapper(TokenResponse tokenResponse, TokenResponseError tokenResponseError) {
        this.tokenResponse = tokenResponse;
        this.tokenResponseError = tokenResponseError;
    }

    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    public TokenResponseError getTokenResponseError() {
        return tokenResponseError;
    }

    public boolean isOk() {
        return tokenResponse != null;
    }

    public boolean isError() {
        return tokenResponseError != null;
    }

    public static TokenResponseWrapper ok(TokenResponse tokenResponse) {
        return new TokenResponseWrapper(tokenResponse, null);
    }

    public static TokenResponseWrapper error(TokenResponseError tokenResponseError) {
        return new TokenResponseWrapper(null, tokenResponseError);
    }

}
