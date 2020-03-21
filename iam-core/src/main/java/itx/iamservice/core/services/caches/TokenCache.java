package itx.iamservice.core.services.caches;

import itx.iamservice.core.services.dto.JWToken;

/**
 * Cache service for revoked token storage.
 * This cache stores revoked but still valid JWT tokens,
 * so resource owners may verify token validity in real-time.
 */
public interface TokenCache {

    /**
     * Add revoked token into cache.
     * @param jwToken
     */
    void addRevokedToken(JWToken jwToken);

    /**
     * Remove expired tokens from this cache. Expired tokens may be removed from this cache,
     * because such tokens are not valid anymore.
     * @return number of tokens removed.
     */
    int purgeRevokedTokens();

    /**
     * Verify is token has been revoked.
     * @param jwToken {@link JWToken} to be validated.
     * @return true if provided token has been revoked, false otherwise.
     */
    boolean isRevoked(JWToken jwToken);

    /**
     * Get number of revoked tokens in this cache.
     * @return number of revoked tokens in this cache.
     */
    int size();

}
