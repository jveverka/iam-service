package one.microproject.iamservice.core.services;

import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.services.dto.IdTokenRequest;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.services.dto.RevokeTokenRequest;
import one.microproject.iamservice.core.services.dto.Scope;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.services.dto.UserInfoResponse;

import java.net.URI;
import java.util.Optional;

public interface AuthenticationService {

    /**
     * Authenticate end-user in username/password credentials flow.
     * grant_type=password
     * @param organizationId
     * @param projectId
     * @param upAuthenticationRequest
     * @param idTokenRequest
     * @return
     */
    Optional<TokenResponse> authenticate(URI issuerUri, OrganizationId organizationId, ProjectId projectId,
                                         ClientCredentials clientCredentials, Scope scope,
                                         UPAuthenticationRequest upAuthenticationRequest,
                                         IdTokenRequest idTokenRequest);

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
    Optional<TokenResponse> authenticate(URI issuerUri, OrganizationId organizationId, ProjectId projectId,
                                         ClientCredentials clientCredentials, Scope scope,
                                         IdTokenRequest idTokenRequest);

    /**
     * Authenticate end-user authorization code grant.
     * grant_type=authorization_code
     * @param code
     * @param idTokenRequest
     * @return
     */
    Optional<TokenResponse> authenticate(Code code, IdTokenRequest idTokenRequest);

    /**
     * Set scope in existing authorization code grant flow.
     * @param code
     * @param scope - updated scope
     * @return
     */
    Optional<AuthorizationCodeContext> setScope(Code code, Scope scope);

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
    Optional<AuthorizationCode> login(URI issuerUri, OrganizationId organizationId, ProjectId projectId, UserId userId, ClientId clientId, String password,
                                      Scope scope, String state, String redirectURI, String codeChallenge, String codeChallengeMethod);

    /**
     * Logout client action revokes validity of issued {@link JWToken}.
     * In case provided JWToken is still valid, it is blacklisted and considered invalid for further use.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param request issued and valid {@link JWToken}
     * @return true in case provided {@link JWToken} is valid, false otherwise.
     */
    boolean revoke(OrganizationId organizationId, ProjectId projectId, RevokeTokenRequest request);

    /**
     * Get user info for issued JWT specified in @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#UserInfoRequest">OIDC core 1.0</a>
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param token issued and valid {@link JWToken}
     * @return {@link UserInfoResponse} in case user exists and provided {@link JWToken} is valid.
     */
    Optional<UserInfoResponse> getUserInfo(OrganizationId organizationId, ProjectId projectId, JWToken token);

}
