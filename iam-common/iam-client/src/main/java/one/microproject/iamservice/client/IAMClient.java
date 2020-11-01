package one.microproject.iamservice.client;

import one.microproject.iamservice.client.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * IAM-Service remote client.
 */
public interface IAMClient extends AutoCloseable {

    /**
     * Wait until IAMClient is fully initialized.
     * @param timeout - timeout wait interval duration.
     * @param timeUnit - timeout unit.
     * @return - true, if initialization  has succeeded in given time interval, false otherwise.
     * @throws InterruptedException in case waiting time is exceeded.
     */
    boolean waitForInit(long timeout, TimeUnit timeUnit) throws InterruptedException;

    /**
     * Validate JWT. This method validates:
     * - token's expiration time.
     * - token's signature, using public key identified by "kid".
     * @param token - a JWT token to validate.
     * @return {@link Optional} of {@link StandardTokenClaims} - empty only if token validation fails, present if token validation is ok.
     */
    Optional<StandardTokenClaims> validate(JWToken token);

    /**
     * Validate JWT. This method validates:
     * - token's expiration time.
     * - token's signature, using public key identified by "kid".
     * - token's issuer matches expected value "[baseUrl]/organizationId/projectId".
     * @param organizationId - valid/existing {@link OrganizationId}
     * @param projectId - valid/existing {@link ProjectId}
     * @param token - a JWT token to validate.
     * @return {@link Optional} of {@link StandardTokenClaims} - empty only if token validation fails, present if token validation is ok.
     */
    Optional<StandardTokenClaims> validate(OrganizationId organizationId, ProjectId projectId, JWToken token);

    /**
     * Validate JWT. This method validates:
     * - token's expiration time.
     * - token's signature, using public key identified by "kid".
     * - token's issuer matches expected value "[baseUrl]/organizationId/projectId".
     * - token's scope matches expected admin or application permission set. Admin permission set has priority over application permission set.
     * @param organizationId - valid/existing {@link OrganizationId}
     * @param projectId - valid/existing {@link ProjectId}
     * @param requiredAdminPermissions - minimal required set of admin permissions.
     * @param requiredApplicationPermissions - minimal required set of application permissions.
     * @param token - a JWT token to validate.
     * @return false only if token validation fails, true if token validation is ok.
     */
    boolean validate(OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions, JWToken token);

    /**
     * Validate JWT. This method validates:
     * - token's expiration time.
     * - token's signature, using public key identified by "kid".
     * - token's issuer matches expected value "[baseUrl]/organizationId/projectId".
     * - token's scope matches expected application permissions.
     * @param organizationId - valid/existing {@link OrganizationId}
     * @param projectId - valid/existing {@link ProjectId}
     * @param requiredApplicationPermissions - minimal required set of application permissions.
     * @param token - a JWT token to validate.
     * @return false only if token validation fails, true if token validation is ok.
     */
    boolean validate(OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredApplicationPermissions, JWToken token);

    /**
     * Force update of internal JWK key cache.
     */
    void updateKeyCache();

}
