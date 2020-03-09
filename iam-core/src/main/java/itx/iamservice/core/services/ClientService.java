package itx.iamservice.core.services;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.dto.JWToken;

import java.util.Optional;

/**
 * Service providing client authentication and token invalidation (revoke).
 * This service is intended to be used mainly by OAuth2 "client" roles.
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-1.1">OAuth2 roles</a>
 */
public interface ClientService {

    /**
     * Authenticate client and provide valid {@link JWToken} in case authentication has been successful.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param authenticationRequest request containing client's credentials.
     * @return valid {@link JWToken} in case authentication has been successful, empty otherwise.
     */
    Optional<JWToken> authenticate(OrganizationId organizationId, ProjectId projectId, AuthenticationRequest authenticationRequest);

    /**
     * Request new instance of JWToken before issued token expires.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param token previously issued and valid {@link JWToken}
     * @return new instance of {@link JWToken} or empty if provided {@link JWToken} was not valid.
     */
    Optional<JWToken> renew(OrganizationId organizationId, ProjectId projectId, JWToken token);

    /**
     * Logout client action revokes validity of issued {@link JWToken}.
     * In case provided JWToken is still valid, it is blacklisted and considered invalid for further use.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param token issued and valid {@link JWToken}
     * @return true in case provided {@link JWToken} is valid, false otherwise.
     */
    boolean logout(OrganizationId organizationId, ProjectId projectId, JWToken token);

}
