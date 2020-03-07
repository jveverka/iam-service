package itx.iamservice.core.services.impl;

import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.dto.JWToken;

import java.util.Optional;

public class ClientServiceImpl implements ClientService {

    private final Model model;
    private final TokenCache tokenCache;

    public ClientServiceImpl(Model model, TokenCache tokenCache) {
        this.model = model;
        this.tokenCache = tokenCache;
    }

    @Override
    public Optional<JWToken> authenticate(AuthenticationRequest authenticationRequest) {
        Optional<Client> client = model.getClient(authenticationRequest.getClientId());
        if (client.isPresent()) {

        }
        return Optional.empty();
    }

    @Override
    public Optional<JWToken> renew(JWToken token) {
        return Optional.empty();
    }

    @Override
    public boolean logout(JWToken token) {
        //TODO: validate if toke is already not expired
        tokenCache.addRevokedToken(token);
        return true;
    }
}
