package one.microproject.iamservice.core;

import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;

import java.security.PublicKey;
import java.util.Optional;
import java.util.Set;

public interface TokenValidator {

    /**
     * Validate JWT using provided {@link PublicKey}
     * @param key - provided {@link PublicKey} for signature verification.
     * @param token - an instance of JWT to be verified.
     * @return {@link Optional} of {@link StandardTokenClaims} in case token is valid, {@link Optional#empty()} in case provided JWT is not valid.
     */
    Optional<StandardTokenClaims> validateToken(PublicKey key, JWToken token);

    /**
     * Validate JWT using provided {@link KeyProvider}
     * @param keyProvider - {@link KeyProvider} to get Key for signature verification.
     * @param token - an instance of JWT to be verified.
     * @return {@link Optional} of {@link StandardTokenClaims} in case token is valid, {@link Optional#empty()} in case provided JWT is not valid.
     */
    Optional<StandardTokenClaims> validateToken(KeyProvider keyProvider, JWToken token);

    /**
     * Extract token from http 'Authorization' header.
     * @param authorization - 'Authorization' string in format "Bearer: 'JWT_TOKEN'"
     * @return - an instance of JWT.
     */
    JWToken extractJwtToken(String authorization);

    /**
     * Validate JWT for organization and project.
     * @param organizationId - unique organization ID
     * @param projectId - unique project ID
     * @param response - collection of JWKs
     * @param token - JWT token to validate.
     * @return {@link Optional} of {@link StandardTokenClaims} in case token validation is OK, empty otherwise.
     */
    Optional<StandardTokenClaims> validateToken(OrganizationId organizationId, ProjectId projectId, JWKResponse response, JWToken token);

    /**
     * Validate JWT for organization and project.
     * @param organizationId - unique organization ID
     * @param projectId - unique project ID
     * @param response - collection of JWKs
     * @param requiredAdminPermissions - expected set of admin permissions.
     * @param requiredApplicationPermissions - expected set of application permissions.
     * @param token - JWT token to validate.
     * @return true in case token validation is OK, false otherwise.
     */
    boolean validateToken(OrganizationId organizationId, ProjectId projectId, JWKResponse response, Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions, JWToken token);

}
