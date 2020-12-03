package one.microproject.iamservice.core.services.impl;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.KeyPairData;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.model.Tokens;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.utils.TokenUtils;
import one.microproject.iamservice.core.services.TokenGenerator;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.services.dto.IdTokenRequest;
import one.microproject.iamservice.core.services.dto.Scope;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TokenGeneratorImpl implements TokenGenerator {

    @Override
    public TokenResponse generate(URI issuerUri, OrganizationId organizationId, Project project, User user, Set<Permission> userPermissions, Scope scope, ClientId clientId, IdTokenRequest idTokenRequest) {
        KeyPairData keyPairData = user.getKeyPairData();
        Scope filteredScopes = TokenUtils.filterScopes(userPermissions, scope);
        JWToken accessToken = TokenUtils.issueToken(issuerUri, organizationId, project.getId(), project.getAudience(), user.getId(),
                user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.BEARER);
        JWToken refreshToken = TokenUtils.issueToken(issuerUri, organizationId, project.getId(), project.getAudience(), user.getId(),
                user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.REFRESH);
        JWToken idToken = TokenUtils.issueIdToken(issuerUri, organizationId, project.getId(), clientId,
                user.getId().getId(), user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, idTokenRequest,
                keyPairData.getId(), keyPairData.getPrivateKey());
        Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                user.getDefaultAccessTokenDuration()/1000L, user.getDefaultRefreshTokenDuration()/1000L, idToken);
        return getTokenResponse(tokens);
    }

    @Override
    public TokenResponse generate(URI issuerUri, OrganizationId organizationId, Project project, Set<Permission> clientPermissions, Client client, Scope scope, IdTokenRequest idTokenRequest) {
        Scope filteredScopes = TokenUtils.filterScopes(clientPermissions, scope);
        KeyPairData keyPairData = project.getKeyPairData();
        JWToken accessToken = TokenUtils.issueToken(issuerUri, organizationId, project.getId(), project.getAudience(), client.getId(),
                client.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.BEARER);
        JWToken refreshToken = TokenUtils.issueToken(issuerUri, organizationId, project.getId(), project.getAudience(), client.getId(),
                client.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.REFRESH);
        JWToken idToken = TokenUtils.issueIdToken(issuerUri, organizationId, project.getId(), client.getId(), client.getId().getId(),
                client.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, idTokenRequest,
                keyPairData.getId(), keyPairData.getPrivateKey());
        Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                client.getDefaultAccessTokenDuration()/1000L, client.getDefaultRefreshTokenDuration()/1000L, idToken);
        return getTokenResponse(tokens);
    }

    @Override
    public TokenResponse generate(AuthorizationCodeContext context, User user, IdTokenRequest idTokenRequest) {
        KeyPairData keyPairData = user.getKeyPairData();
        JWToken accessToken = TokenUtils.issueToken(context.getIssuerUri(), context.getOrganizationId(), context.getProjectId(), context.getAudience(), user.getId(),
                user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, context.getScope(),
                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.BEARER);
        JWToken refreshToken = TokenUtils.issueToken(context.getIssuerUri(), context.getOrganizationId(), context.getProjectId(), context.getAudience(), user.getId(),
                user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, context.getScope(),
                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.REFRESH);
        JWToken idToken = TokenUtils.issueIdToken(context.getIssuerUri(), context.getOrganizationId(), context.getProjectId(), context.getClientId(), user.getId().getId(),
                user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, idTokenRequest,
                keyPairData.getId(), keyPairData.getPrivateKey());
        Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                user.getDefaultAccessTokenDuration() / 1000L, user.getDefaultRefreshTokenDuration() / 1000L, idToken);
        return getTokenResponse(tokens);
    }

    private static TokenResponse getTokenResponse(Tokens tokens) {
        return new TokenResponse(
                tokens.getAccessToken().getToken(),
                tokens.getExpiresIn(),
                tokens.getRefreshExpiresIn(),
                tokens.getRefreshToken().getToken(),
                tokens.getTokenType().getType(),
                tokens.getIdToken().getToken()
        );
    }

}
