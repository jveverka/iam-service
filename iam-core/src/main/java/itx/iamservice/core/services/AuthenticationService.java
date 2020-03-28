package itx.iamservice.core.services;

import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.JWToken;
import itx.iamservice.core.services.dto.TokenResponse;

import java.util.Optional;
import java.util.Set;

public interface AuthenticationService {

    /**
     * Authenticate end-user in username/password flow.
     * grant_type=password
     * @param organizationId
     * @param projectId
     * @param clientCredentials
     * @param upAuthenticationRequest
     * @param scopes
     * @return
     */
    Optional<TokenResponse> authenticate(OrganizationId organizationId, ProjectId projectId,
                                         ClientCredentials clientCredentials, UPAuthenticationRequest upAuthenticationRequest, Set<RoleId> scopes);

    /**
     * Authenticate end-user in client credentials flow.
     * grant_type=client_credentials
     * @param organizationId
     * @param projectId
     * @param clientCredentials
     * @param scopes
     * @return
     */
    Optional<TokenResponse> authenticate(OrganizationId organizationId, ProjectId projectId,
                                         ClientCredentials clientCredentials, Set<RoleId> scopes);

    /**
     * Authenticate end-user authorization code grant.
     * grant_type=authorization_code
     * @param code
     * @return
     */
    Optional<TokenResponse> authenticate(Code code);

    /**
     * Get new set of tokens using issued and valid refresh toke.
     * @param organizationId
     * @param projectId
     * @param refreshToken
     * @param clientCredentials
     * @param scopes
     * @return
     */
    Optional<TokenResponse> refreshTokens(OrganizationId organizationId, ProjectId projectId, JWToken refreshToken,
                                         ClientCredentials clientCredentials, Set<RoleId> scopes);

    /**
     * Get short-lived authorization_code based on clientId, username=userId and password
     * grant_type=authorization_code
     * @param organizationId
     * @param projectId
     * @param userId
     * @param clientId
     * @param password
     * @param scope
     * @param state
     * @return
     */
    Optional<AuthorizationCode> login(OrganizationId organizationId, ProjectId projectId, UserId userId, ClientId clientId, String password, String scope, String state);

}
