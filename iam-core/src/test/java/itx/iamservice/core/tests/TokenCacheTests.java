package itx.iamservice.core.tests;

import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.model.TokenCacheImpl;
import org.junit.jupiter.api.BeforeAll;

public class TokenCacheTests {

    private static TokenCache tokenCache;

    @BeforeAll
    private void init() {
        tokenCache = new TokenCacheImpl();
    }

}
