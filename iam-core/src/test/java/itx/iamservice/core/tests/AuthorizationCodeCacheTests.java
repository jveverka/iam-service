package itx.iamservice.core.tests;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.dto.Scope;
import itx.iamservice.core.services.impl.caches.AuthorizationCodeCacheImpl;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.AuthorizationCodeContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorizationCodeCacheTests {

    private static Long maxDuration = 3L;
    private static TimeUnit timeUnit = TimeUnit.SECONDS;
    private static AuthorizationCodeCache authorizationCodeCache;
    private static Set<String> audience = Set.of("aud1", "aud2");
    private static final Scope scope = new Scope(Set.of("manage-organizations", "manage-projects", "not-existing-role"));

    private static AuthorizationCode authorizationCode;

    @BeforeAll
    private static void init() {
        authorizationCodeCache = new AuthorizationCodeCacheImpl(maxDuration, timeUnit);
    }

    @Test
    @Order(1)
    public void testCacheAfterInit() {
        int purged = authorizationCodeCache.purgeCodes();
        assertEquals(0, purged);
    }

    @Test
    @Order(2)
    public void testIssueCode() {
        authorizationCode = authorizationCodeCache.issue(OrganizationId.from("org01"), ProjectId.from("proj01"),
                ClientId.from("cl01"), UserId.from("usr01"), UUID.randomUUID().toString(), scope, audience);
        assertNotNull(authorizationCode);
        Optional<AuthorizationCodeContext> verifiedAuthorizationCode = authorizationCodeCache.verifyAndRemove(authorizationCode.getCode());
        assertTrue(verifiedAuthorizationCode.isPresent());
        verifiedAuthorizationCode = authorizationCodeCache.verifyAndRemove(authorizationCode.getCode());
        assertTrue(verifiedAuthorizationCode.isEmpty());
        int purged = authorizationCodeCache.purgeCodes();
        assertEquals(0, purged);
    }

}
