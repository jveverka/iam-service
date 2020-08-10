package itx.iamservice.core.services;

import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.IdTokenRequest;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.dto.Scope;
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
     * @param scope
     * @param idTokenRequest
     * @return
     */
    Optional<TokenResponse> authenticate(OrganizationId organizationId, ProjectId projectId,
                                         ClientCredentials clientCredentials, UPAuthenticationRequest upAuthenticationRequest,
                                         Scope scope, IdTokenRequest idTokenRequest);

    /**
     * Authenticate end-user in client credentials flow.
     * grant_type=client_credentials
     * @param organizationId
     * @param projectId
     * @param clientCredentials
     * @param scope
     * @param idTokenRequest
     * @return
     */
    Optional<TokenResponse> authenticate(OrganizationId organizationId, ProjectId projectId,
                                         ClientCredentials clientCredentials, Scope scope, IdTokenRequest idTokenRequest);

    /**
     * Authenticate end-user authorization code grant.
     * grant_type=authorization_code
     * @param code
     * @param idTokenRequest
     * @return
     */
    Optional<TokenResponse> authenticate(Code code, IdTokenRequest idTokenRequest);

    /**
     * Get new set of tokens using issued and valid refresh toke.
     * @param organizationId
     * @param projectId
     * @param refreshToken
     * @param clientCredentials
     * @param scope
     * @param idTokenRequest
     * @return
     */
    Optional<TokenResponse> refreshTokens(OrganizationId organizationId, ProjectId projectId, JWToken refreshToken,
                                         ClientCredentials clientCredentials, Scope scope, IdTokenRequest idTokenRequest);

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
