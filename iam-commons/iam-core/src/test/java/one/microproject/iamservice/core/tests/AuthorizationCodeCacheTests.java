package one.microproject.iamservice.core.tests;

import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKCEMethod;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.caches.AuthorizationCodeCache;
import one.microproject.iamservice.core.services.dto.Scope;
import one.microproject.iamservice.core.services.impl.caches.AuthorizationCodeCacheImpl;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.services.impl.caches.CacheHolderImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorizationCodeCacheTests {

    private static Long maxDuration = 3L;
    private static TimeUnit timeUnit = TimeUnit.SECONDS;
    private static AuthorizationCodeCache authorizationCodeCache;
    private static Set<String> audience = Set.of("aud1", "aud2");
    private static final Scope scope = new Scope(Set.of("manage-organizations", "manage-projects", "not-existing-role"));

    private static AuthorizationCode authorizationCode;
    private static URI issuerUri;

    @BeforeAll
    private static void init() throws URISyntaxException {
        authorizationCodeCache = new AuthorizationCodeCacheImpl(maxDuration, timeUnit, new CacheHolderImpl<>());
        issuerUri = new URI("http://localhost:8080/issuer");
    }

    @Test
    @Order(1)
    void testCacheAfterInit() {
        int purged = authorizationCodeCache.purgeCodes();
        assertEquals(0, purged);
    }

    @Test
    @Order(2)
    void testIssueCode() {
        Code code = Code.from(UUID.randomUUID().toString());
        AuthorizationCodeContext authorizationCodeContext =
                new AuthorizationCodeContext(code, issuerUri, OrganizationId.from("org01"), ProjectId.from("proj01"),
                        ClientId.from("cl01"), UserId.from("usr01"), UUID.randomUUID().toString(), new Date(), scope, audience,
                        "", "", PKCEMethod.PLAIN);
        authorizationCode = authorizationCodeCache.save(code, authorizationCodeContext);
        assertNotNull(authorizationCode);
        Optional<AuthorizationCodeContext> verifiedAuthorizationCode = authorizationCodeCache.verifyAndRemove(authorizationCode.getCode());
        assertTrue(verifiedAuthorizationCode.isPresent());
        verifiedAuthorizationCode = authorizationCodeCache.verifyAndRemove(authorizationCode.getCode());
        assertTrue(verifiedAuthorizationCode.isEmpty());
        int purged = authorizationCodeCache.purgeCodes();
        assertEquals(0, purged);
    }

}
