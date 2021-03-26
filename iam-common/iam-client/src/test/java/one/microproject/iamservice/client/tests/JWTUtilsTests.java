package one.microproject.iamservice.client.tests;

import one.microproject.iamservice.client.JWTUtils;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.TokenType;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Set;

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

    @Test
    void testCreateAuthorizationHeader() {
        String token = JWTUtils.createAuthorizationHeader("token");
        assertEquals("Bearer token", token);
    }

    @Test
    void testValidatePermissions() throws URISyntaxException {
        StandardTokenClaims standardTokenClaims = new StandardTokenClaims("",  "", "", Set.of(), Set.of(),
                OrganizationId.from(""), ProjectId.from(""), TokenType.BEARER);
        boolean result = JWTUtils.validatePermissions(standardTokenClaims, Set.of(), Set.of());
        assertEquals(true, result);
    }

}
