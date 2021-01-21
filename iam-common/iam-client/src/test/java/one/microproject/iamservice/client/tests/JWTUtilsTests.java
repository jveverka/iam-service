package one.microproject.iamservice.client.tests;

import one.microproject.iamservice.client.JWTUtils;
import one.microproject.iamservice.core.model.JWToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JWTUtilsTests {

    @Test
    void testExtractToken() {
        JWToken token = JWTUtils.extractJwtToken("Bearer token");
        assertNotNull(token);
        assertEquals("token", token.getToken());
        token = JWTUtils.extractJwtToken("Bearer token ");
        assertNotNull(token);
        assertEquals("token", token.getToken());
        token = JWTUtils.extractJwtToken("Bearer ");
        assertNotNull(token);
        assertEquals("", token.getToken());
    }

    @Test
    void testCreateHeader() {
        String authorizationHeader = JWTUtils.createAuthorizationHeader("token");
        assertEquals("Bearer token", authorizationHeader);
        authorizationHeader = JWTUtils.createAuthorizationHeader("  token");
        assertEquals("Bearer token", authorizationHeader);
        authorizationHeader = JWTUtils.createAuthorizationHeader("token  ");
        assertEquals("Bearer token", authorizationHeader);
        authorizationHeader = JWTUtils.createAuthorizationHeader("  token  ");
        assertEquals("Bearer token", authorizationHeader);
    }

}
