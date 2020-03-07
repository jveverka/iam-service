package itx.iamservice.services.impl;

import itx.iamservice.model.Model;
import itx.iamservice.model.TokenCache;
import itx.iamservice.services.ResourceServerService;
import itx.iamservice.services.dto.JWToken;

public class ResourceServerServiceImpl implements ResourceServerService {

    private final Model model;
    private final TokenCache tokenCache;

    public ResourceServerServiceImpl(Model model, TokenCache tokenCache) {
        this.model = model;
        this.tokenCache = tokenCache;
    }

    @Override
    public boolean verify(JWToken token) {
        return false;
    }

}
