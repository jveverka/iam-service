package itx.iamservice.core.tests;

import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.model.TokenCacheImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenCacheTests {

    private static TokenCache tokenCache;

    @BeforeAll
    private static void init() {
        tokenCache = new TokenCacheImpl();
    }

    @Test
    @Order(1)
    public void testToken() {

    }


}
