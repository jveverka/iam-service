package itx.iamservice.core.services;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Tokens;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.IdTokenRequest;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.dto.RevokeTokenRequest;
import itx.iamservice.core.services.dto.Scope;

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
     * @param clientCredentials issued client's credentials.
     * @param scope requested client scope.
     * @param idTokenRequest
     * @return valid {@link Tokens} in case authentication has been successful, empty otherwise.
     */
    Optional<Tokens> authenticate(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials, Scope scope, IdTokenRequest idTokenRequest);

    /**
     * Authenticate user and provide valid {@link JWToken} in case authentication has been successful.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param authenticationRequest request containing user's credentials.
     * @param idTokenRequest
     * @return valid {@link Tokens} in case authentication has been successful, empty otherwise.
     */
    Optional<Tokens> authenticate(OrganizationId organizationId, ProjectId projectId, AuthenticationRequest authenticationRequest, IdTokenRequest idTokenRequest);

    /**
     * Request new instance of JWToken before issued token expires.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param clientCredentials issued client's credentials.
     * @param token previously issued and valid {@link JWToken}
     * @param idTokenRequest
     * @return new instance of {@link Tokens} or empty if provided {@link Tokens} was not valid.
     */
    Optional<Tokens> refresh(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials, JWToken token, Scope scope, IdTokenRequest idTokenRequest);

    /**
     * Login user using userId / password and clientId.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId projectId {@link ProjectId} unique project ID.
     * @param userId user's unique identifier.
     * @param clientId client's unique identifier.
     * @param password user's password.
     * @param scope requested client scope.
     * @param state client's state.
     * @return {@link AuthorizationCode} single use authorization code token.
     */
    Optional<AuthorizationCode> login(OrganizationId organizationId, ProjectId projectId, UserId userId, ClientId clientId, String password, Scope scope, String state);

    /**
     * Authenticate user based on provided {@link Code}
     * @param code authorization code.
     * @param idTokenRequest
     * @return valid {@link Tokens} in case authentication has been successful, empty otherwise.
     */
    Optional<Tokens> authenticate(Code code, IdTokenRequest idTokenRequest);

    /**
     * Authenticate user based on provided {@link Code}
     * @param code authorization code.
     * @return valid {@link Tokens} in case authentication has been successful, empty otherwise.
     */
    Optional<Tokens> authenticate(Code code);

    /**
     * Logout client action revokes validity of issued {@link JWToken}.
     * In case provided JWToken is still valid, it is blacklisted and considered invalid for further use.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param request issued and valid {@link JWToken}
     * @return true in case provided {@link JWToken} is valid, false otherwise.
     */
    boolean revoke(OrganizationId organizationId, ProjectId projectId, RevokeTokenRequest request);

}
