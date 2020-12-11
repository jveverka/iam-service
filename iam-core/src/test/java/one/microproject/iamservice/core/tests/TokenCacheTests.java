package one.microproject.iamservice.core.tests;

import one.microproject.iamservice.core.model.KeyId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.caches.TokenCache;
import one.microproject.iamservice.core.services.dto.Scope;
import one.microproject.iamservice.client.impl.TokenValidatorImpl;
import one.microproject.iamservice.core.services.impl.caches.CacheHolderImpl;
import one.microproject.iamservice.core.services.impl.caches.TokenCacheImpl;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.model.utils.TokenUtils;
import one.microproject.iamservice.core.model.JWToken;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenCacheTests {

    private static final OrganizationId ORGANIZATION_ID = OrganizationId.from("unique-organization-id");
    private static final ProjectId PROJECT_ID = ProjectId.from("unique-project-id");
    private static final UserId USER_ID = UserId.from("unique-user-id");
    private static final Set<String> ROLES = Set.of("role-a", "role-b", "role-c");
    private static final Set<String> AUDIENCE = Set.of("audience");
    private static final Long DURATION = 3L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final Scope SCOPE = Scope.empty();

    private static KeyId keyId;
    private static KeyPair keyPair;
    private static ModelCache modelCache;
    private static TokenCache tokenCache;
    private static JWToken jwToken;
    private static URI issuerUri;

    @BeforeAll
    private static void init() throws NoSuchAlgorithmException, NoSuchProviderException, PKIException, URISyntaxException {
        Security.addProvider(new BouncyCastleProvider());
        keyPair = TokenUtils.generateKeyPair();
        modelCache = ModelUtils.createDefaultModelCache("top-secret", "top-secret", "admin@email.com", Boolean.FALSE);
        tokenCache = new TokenCacheImpl(modelCache, new TokenValidatorImpl(), new CacheHolderImpl<>());
        keyId = KeyId.from("key-001");
        issuerUri = new URI("http://localhost:8080/" + ORGANIZATION_ID.getId() + "/" + PROJECT_ID.getId());
        Map<String, Set<String>> roleClaims = new HashMap<>();
        roleClaims.put(TokenUtils.ROLES_CLAIM, ROLES);
        jwToken = TokenUtils.issueToken(issuerUri, ORGANIZATION_ID, PROJECT_ID, AUDIENCE, USER_ID, DURATION, TIME_UNIT, SCOPE, roleClaims, keyId, keyPair.getPrivate(), TokenType.BEARER);
    }

    @Test
    @Order(1)
    public void afterInitializationTest() {
        int size = tokenCache.size();
        assertEquals(0,size);
        assertFalse(tokenCache.isRevoked(jwToken));
    }

    @Test
    @Order(2)
    public void addRevokedTokenTest() {
        assertFalse(tokenCache.isRevoked(jwToken));
        tokenCache.addRevokedToken(jwToken);
        assertEquals(1, tokenCache.size());
        assertTrue(tokenCache.isRevoked(jwToken));
    }

    @Test
    @Order(3)
    public void waitForTokensToExpireTest() throws InterruptedException {
        while(tokenCache.purgeRevokedTokens() > 0) {
            Thread.sleep(1000);
        };
        int size = tokenCache.size();
        assertEquals(0, size);
    }

    @Test
    @Order(4)
    public void afterCachePurgeTest() {
        assertFalse(tokenCache.isRevoked(jwToken));
    }

}
