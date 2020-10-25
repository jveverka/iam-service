package itx.iamservice.serviceclient;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.serviceclient.impl.AuthenticationException;
import itx.iamservice.serviceclient.impl.ProjectInfoProvider;

import java.net.URL;
import java.util.Set;

public interface IAMAuthorizerClient extends ProjectInfoProvider  {

    /**
     * 1. OAuth2AuthorizationCodeGrant - get authorization code and available scopes
     * @param userName
     * @param password
     * @param clientId
     * @param redirectUri
     * @param state
     * @return
     * @throws AuthenticationException
     */
    AuthorizationCode getAuthorizationCodeOAuth2AuthorizationCodeGrant(String userName, String password, ClientId clientId, Set<String> scopes, URL redirectUri, String state) throws AuthenticationException;

    /**
     * 2. OAuth2AuthorizationCodeGrant - set scopes (consent screen feedback)
     * @param authorizationCode
     * @throws AuthenticationException
     */
    void setOAuth2AuthorizationCodeGrantConsent(AuthorizationCode authorizationCode) throws AuthenticationException;

    /**
     * 3. OAuth2AuthorizationCodeGrant - get access tokens
     * @param code
     * @return
     * @throws AuthenticationException
     */
    TokenResponse getAccessTokensOAuth2AuthorizationCodeGrant(Code code) throws AuthenticationException;

    /**
     * OAuth2UsernamePassword flow - get access tokens
     * @param userName
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     * @throws AuthenticationException
     */
    TokenResponse getAccessTokensOAuth2UsernamePassword(String userName, String password, ClientId clientId, String clientSecret) throws AuthenticationException;

    /**
     * OAuth2ClientCredentials flow - get access tokens
     * @param clientId
     * @param clientSecret
     * @return
     * @throws AuthenticationException
     */
    TokenResponse getAccessTokensOAuth2ClientCredentials(ClientId clientId, String clientSecret) throws AuthenticationException;

    /**
     * Refresh tokens - get access tokens
     * @param refreshToken
     * @param clientId
     * @param clientSecret
     * @return
     * @throws AuthenticationException
     */
    TokenResponse refreshTokens(String refreshToken, ClientId clientId, String clientSecret) throws AuthenticationException;

}
