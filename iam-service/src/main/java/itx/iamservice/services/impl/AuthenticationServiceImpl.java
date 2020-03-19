package itx.iamservice.services.impl;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Tokens;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.services.AuthenticationService;
import itx.iamservice.services.dto.TokenRequest;
import itx.iamservice.services.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientService clientService;

    public AuthenticationServiceImpl(@Autowired ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Optional<TokenResponse> getTokens(OrganizationId organizationId, ProjectId projectId, TokenRequest tokenRequest) {
        UserId userId = UserId.from(tokenRequest.getUsername());
        Client client = new Client(ClientId.from(tokenRequest.getClientId()), tokenRequest.getClientSecret());
        AuthenticationRequest authenticationRequest = new UPAuthenticationRequest(userId, tokenRequest.getPassword(), tokenRequest.getScopes(), client);
        Optional<Tokens> tokensOptional = clientService.authenticate(organizationId, projectId, authenticationRequest);
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

}
