package one.microproject.iamservice.serviceclient;

import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import one.microproject.iamservice.serviceclient.impl.ProjectInfoProvider;

import java.net.URL;
import java.util.Set;

public interface IAMAuthorizerClient extends ProjectInfoProvider {

    /**
     * 1. OAuth2AuthorizationCodeGrant - get authorization code and available scopes
     * @param userName unique user's ID
     * @param password user's password.
     * @param clientId unique client ID.
     * @param scopes set of requested scopes.
     * @param redirectUri URI for redirection.
     * @param state random state string.
     * @return valid authorization code.
     * @throws AuthenticationException in case provided credentials and user/client IDs are invalid or not recognized.
     */
    AuthorizationCode getAuthorizationCodeOAuth2AuthorizationCodeGrant(String userName, String password, ClientId clientId, Set<String> scopes, URL redirectUri, String state) throws AuthenticationException;

    /**
     * 2. OAuth2AuthorizationCodeGrant - set scopes (consent screen feedback)
     * @param authorizationCode valid authorization code.
     * @throws AuthenticationException in case provided credentials and user/client IDs are invalid or not recognized.
     */
    void setOAuth2AuthorizationCodeGrantConsent(AuthorizationCode authorizationCode) throws AuthenticationException;

    /**
     * 3. OAuth2AuthorizationCodeGrant - get access tokens
     * @param code valid authorization code.
     * @return set of tokens (access_token, refresh_token, id_token)
     * @throws AuthenticationException in case provided credentials and user/client IDs are invalid or not recognized.
     */
    TokenResponse getAccessTokensOAuth2AuthorizationCodeGrant(Code code, String state) throws AuthenticationException;

    /**
     * OAuth2UsernamePassword flow - get access tokens
     * @param userName unique user's ID
     * @param password user's password.
     * @param clientId unique client ID.
     * @param clientSecret client secret for client ID.
     * @return set of tokens (access_token, refresh_token, id_token)
     * @throws AuthenticationException in case provided credentials and user/client IDs are invalid or not recognized.
     */
    TokenResponse getAccessTokensOAuth2UsernamePassword(String userName, String password, ClientId clientId, String clientSecret) throws AuthenticationException;

    /**
     * OAuth2ClientCredentials flow - get access tokens
     * @param clientId unique client ID.
     * @param clientSecret client secret for client ID.
     * @return set of tokens (access_token, refresh_token, id_token)
     * @throws AuthenticationException in case provided credentials and user/client IDs are invalid or not recognized.
     */
    TokenResponse getAccessTokensOAuth2ClientCredentials(ClientId clientId, String clientSecret) throws AuthenticationException;

    /**
     * Refresh tokens - get access tokens
     * @param refreshToken valid, previously issued refresh_token
     * @param clientId unique client ID.
     * @param clientSecret client secret for client ID.
     * @return set of tokens (access_token, refresh_token, id_token)
     * @throws AuthenticationException in case provided credentials and user/client IDs are invalid or not recognized.
     */
    TokenResponse refreshTokens(String refreshToken, ClientId clientId, String clientSecret) throws AuthenticationException;

}
