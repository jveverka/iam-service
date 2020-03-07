package itx.iamservice.core.services.impl;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.dto.JWToken;

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
