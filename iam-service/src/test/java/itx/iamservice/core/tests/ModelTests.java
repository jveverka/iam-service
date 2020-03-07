package itx.iamservice.core.tests;

import itx.iamservice.model.Model;
import itx.iamservice.model.TokenCache;
import itx.iamservice.model.TokenCacheImpl;
import itx.iamservice.services.ClientService;
import itx.iamservice.services.ResourceServerService;
import itx.iamservice.services.impl.ClientServiceImpl;
import itx.iamservice.model.ModelUtils;
import itx.iamservice.services.impl.ResourceServerServiceImpl;
import org.junit.jupiter.api.BeforeAll;

public class ModelTests {

    private static Model model;
    private static ClientService clientService;
    private static ResourceServerService resourceServerService;
    private static TokenCache tokenCache;

    @BeforeAll
    private void init() {
        model = ModelUtils.createDefaultModel();
        tokenCache = new TokenCacheImpl();
        clientService = new ClientServiceImpl(model, tokenCache);
        resourceServerService = new ResourceServerServiceImpl(model, tokenCache);
    }



}
