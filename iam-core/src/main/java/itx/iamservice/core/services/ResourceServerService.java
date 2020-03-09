package itx.iamservice.core.services;

import itx.iamservice.core.services.dto.JWToken;

/**
 *  Service providing verification of {@link JWToken} instances.
 *  This service is intended to be used mainly by OAuth2 "resource server" roles.
 *  @see <a href="https://tools.ietf.org/html/rfc6749#section-1.1">OAuth2 roles</a>
 */
public interface ResourceServerService {

    /**
     * Verify if JWT token is valid. Time stamps and signature is verified.
     * @param token {@link JWToken} to verify.
     * @return true if provided {@link JWToken} is valid, false otherwise.
     */
    boolean verify(JWToken token);

}
