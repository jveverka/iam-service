package one.microproject.iamservice.core.model;

/**
 * OAuth2 PKCE code_challenge_method.
 * https://tools.ietf.org/html/rfc7636#section-4.2
 */
public enum PKCEMethod {

    S256("S256"),
    PLAIN("plain");

    private final String type;

    PKCEMethod(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static PKCEMethod getPKCEMethod(String typeName) {
        switch (typeName) {
            case "S256":
                return S256;
            case "plain":
                return PLAIN;
            default:
                throw new UnsupportedOperationException("Unsupported code_challenge_method " + typeName);
        }
    }
}
