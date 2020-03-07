package itx.iamservice.services.impl;

import itx.iamservice.model.AuthenticationRequest;
import itx.iamservice.model.Client;
import itx.iamservice.model.Model;
import itx.iamservice.model.TokenCache;
import itx.iamservice.services.ClientService;
import itx.iamservice.services.dto.JWToken;

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
