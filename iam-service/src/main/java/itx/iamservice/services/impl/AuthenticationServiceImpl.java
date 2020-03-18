package itx.iamservice.services.impl;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.dto.JWToken;
import itx.iamservice.services.AuthenticationService;
import itx.iamservice.services.dto.TokenRequest;
import itx.iamservice.services.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientService clientService;

    public AuthenticationServiceImpl(@Autowired ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Optional<TokenResponse> getTokens(OrganizationId organizationId, ProjectId projectId, TokenRequest tokenRequest) {
        Set<RoleId> scopes = new HashSet<>();
        String[] rawScopes = tokenRequest.getScope().trim().split(" ");
        for (String scope: rawScopes) {
            scopes.add(RoleId.from(scope));
        }
        ClientId clientId = ClientId.from(tokenRequest.getUsername());
        AuthenticationRequest authenticationRequest = new UPAuthenticationRequest(clientId, tokenRequest.getPassword(), scopes);
        Optional<JWToken> tokenOptional = clientService.authenticate(organizationId, projectId, authenticationRequest);
        if (tokenOptional.isPresent()) {
            TokenResponse tokenResponse = new TokenResponse(tokenOptional.get().getToken(), 600L, 0L, "", "bearer");
            return Optional.of(tokenResponse);
        }
        return Optional.empty();
    }

}
