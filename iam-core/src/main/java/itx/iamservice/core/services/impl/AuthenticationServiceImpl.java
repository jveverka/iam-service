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
import itx.iamservice.core.services.dto.IdTokenRequest;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.dto.Scope;
import itx.iamservice.core.services.dto.TokenResponse;

import java.util.Optional;
import java.util.Set;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientService clientService;

    public AuthenticationServiceImpl(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Optional<TokenResponse> authenticate(OrganizationId organizationId, ProjectId projectId,
                                                ClientCredentials clientCredentials, UPAuthenticationRequest upAuthenticationRequest,
                                                Scope scope, IdTokenRequest idTokenRequest) {
        Optional<Tokens> tokensOptional = clientService.authenticate(organizationId, projectId, upAuthenticationRequest, idTokenRequest);
        if (tokensOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokensOptional.get().getAccessToken().getToken(),
                    tokensOptional.get().getExpiresIn(),
                    tokensOptional.get().getRefreshExpiresIn(),
                    tokensOptional.get().getRefreshToken().getToken(),
                    tokensOptional.get().getTokenType().getType(),
                    tokensOptional.get().getIdToken().getToken());
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenResponse> authenticate(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials,
                                                Scope scope, IdTokenRequest idTokenRequest) {
        Optional<Tokens> tokensOptional = clientService.authenticate(organizationId, projectId, clientCredentials, scope, idTokenRequest);
        if (tokensOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokensOptional.get().getAccessToken().getToken(),
                    tokensOptional.get().getExpiresIn(),
                    tokensOptional.get().getRefreshExpiresIn(),
                    tokensOptional.get().getRefreshToken().getToken(),
                    tokensOptional.get().getTokenType().getType(),
                    tokensOptional.get().getIdToken().getToken());
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenResponse> refreshTokens(OrganizationId organizationId, ProjectId projectId, JWToken refreshToken,
                                                 ClientCredentials clientCredentials, Scope scope, IdTokenRequest idTokenRequest) {
        Optional<Tokens> tokensOptional = clientService.refresh(organizationId, projectId, clientCredentials, refreshToken, scope, idTokenRequest);
        if (tokensOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokensOptional.get().getAccessToken().getToken(),
                    tokensOptional.get().getExpiresIn(),
                    tokensOptional.get().getRefreshExpiresIn(),
                    tokensOptional.get().getRefreshToken().getToken(),
                    tokensOptional.get().getTokenType().getType(),
                    tokensOptional.get().getIdToken().getToken());
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenResponse> authenticate(Code code, IdTokenRequest idTokenRequest) {
        Optional<Tokens> tokensOptional = clientService.authenticate(code, idTokenRequest);
        if (tokensOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokensOptional.get().getAccessToken().getToken(),
                    tokensOptional.get().getExpiresIn(),
                    tokensOptional.get().getRefreshExpiresIn(),
                    tokensOptional.get().getRefreshToken().getToken(),
                    tokensOptional.get().getTokenType().getType(),
                    tokensOptional.get().getIdToken().getToken());
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthorizationCode> login(OrganizationId organizationId, ProjectId projectId, UserId userId, ClientId clientId, String password, String scopes, String state) {
        Scope scope = ModelUtils.getScopes(scopes);
        return clientService.login(organizationId, projectId, userId, clientId, password, scope, state);
    }

}
