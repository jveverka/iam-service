package itx.iamservice.client;

import com.nimbusds.jwt.JWTClaimsSet;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;

import java.util.Optional;
import java.util.Set;

/**
 * IAM-Service remote client.
 */
public interface IAMClient extends AutoCloseable {

    /**
     * Validate JWT. This method validates:
     * - token's expiration time.
     * - token's signature, using public key identified by "kid".
     * @param token - a JWT token to validate.
     * @return {@link Optional} of {@link JWTClaimsSet} - empty only if token validation fails, present if token validation is ok.
     */
    Optional<JWTClaimsSet> validate(JWToken token);

    /**
     * Validate JWT. This method validates:
     * - token's expiration time.
     * - token's signature, using public key identified by "kid".
     * - token's issuer matches expected value "[baseUrl]/organizationId/projectId".
     * @param organizationId - valid/existing {@link OrganizationId}
     * @param projectId - valid/existing {@link ProjectId}
     * @param token - a JWT token to validate.
     * @return {@link Optional} of {@link JWTClaimsSet} - empty only if token validation fails, present if token validation is ok.
     */
    Optional<JWTClaimsSet> validate(OrganizationId organizationId, ProjectId projectId, JWToken token);

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

}
