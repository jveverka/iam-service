package itx.iamservice.client.tests;

import itx.iamservice.client.JWTUtils;
import itx.iamservice.core.model.JWToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JWTUtilsTests {

    @Test
    public void testExtractToken() {
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
    public void testCreateHeader() {
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
