package itx.iamservice.client.spring;

import itx.iamservice.core.model.JWToken;

public final class JWTUtils {

    public static final String BEARER_PREFIX = "Bearer ";

    private JWTUtils() {
        throw new UnsupportedOperationException("Do not instantiate utility class.");
    }

    public static String createAuthorizationHeader(String jwToken) {
        return BEARER_PREFIX + jwToken;
    }

    public static JWToken extractJwtToken(String authorization) {
        return new JWToken(authorization.substring(BEARER_PREFIX.length(), authorization.length()).trim());
    }

}
