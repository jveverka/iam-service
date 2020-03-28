package itx.iamservice.core.services.impl;

import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.Tokens;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.AuthenticationService;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.JWToken;
import itx.iamservice.core.services.dto.TokenResponse;

import java.util.Optional;
import java.util.Set;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientService clientService;

    public AuthenticationServiceImpl(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Optional<TokenResponse> authenticate(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials, UPAuthenticationRequest upAuthenticationRequest, Set<RoleId> scopes) {
        Optional<Tokens> tokensOptional = clientService.authenticate(organizationId, projectId, upAuthenticationRequest);
        if (tokensOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokensOptional.get().getAccessToken().getToken(),
                    tokensOptional.get().getExpiresIn(),
                    tokensOptional.get().getRefreshExpiresIn(),
                    tokensOptional.get().getRefreshToken().getToken(),
                    tokensOptional.get().getTokenType().getType());
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenResponse> authenticate(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials, Set<RoleId> scopes) {
        Optional<Tokens> tokensOptional = clientService.authenticate(organizationId, projectId, clientCredentials, scopes);
        if (tokensOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokensOptional.get().getAccessToken().getToken(),
                    tokensOptional.get().getExpiresIn(),
                    tokensOptional.get().getRefreshExpiresIn(),
                    tokensOptional.get().getRefreshToken().getToken(),
                    tokensOptional.get().getTokenType().getType());
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenResponse> refreshTokens(OrganizationId organizationId, ProjectId projectId, JWToken refreshToken, ClientCredentials clientCredentials, Set<RoleId> scopes) {
        Optional<Tokens> tokensOptional = clientService.refresh(organizationId, projectId, clientCredentials, refreshToken, scopes);
        if (tokensOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokensOptional.get().getAccessToken().getToken(),
                    tokensOptional.get().getExpiresIn(),
                    tokensOptional.get().getRefreshExpiresIn(),
                    tokensOptional.get().getRefreshToken().getToken(),
                    tokensOptional.get().getTokenType().getType());
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenResponse> authenticate(Code code) {
        Optional<Tokens> tokensOptional = clientService.authenticate(code);
        if (tokensOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokensOptional.get().getAccessToken().getToken(),
                    tokensOptional.get().getExpiresIn(),
                    tokensOptional.get().getRefreshExpiresIn(),
                    tokensOptional.get().getRefreshToken().getToken(),
                    tokensOptional.get().getTokenType().getType());
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthorizationCode> login(OrganizationId organizationId, ProjectId projectId, UserId userId, ClientId clientId, String password, String scope, String state) {
        Set<RoleId> scopes = ModelUtils.getScopes(scope);
        return clientService.login(organizationId, projectId, userId, clientId, password, scopes, state);
    }

}
