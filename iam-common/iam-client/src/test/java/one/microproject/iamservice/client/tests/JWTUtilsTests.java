package one.microproject.iamservice.client.tests;

import one.microproject.iamservice.client.JWTUtils;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.PermissionParsingException;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Stream;

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

    private static Stream<Arguments> provideValidatePermissionsData() throws URISyntaxException, PermissionParsingException {
        return Stream.of(
                Arguments.of(createStandardTokenClaims(Set.of()), Set.of(), Set.of(), Boolean.TRUE),
                Arguments.of(createStandardTokenClaims(Set.of("s1.r1.a1", "s2.a2.r2")), Set.of(), Set.of(), Boolean.TRUE),
                Arguments.of(createStandardTokenClaims(Set.of("s1.r1.a1")), Set.of(), Set.of(Permission.from("s1.r1.a1")), Boolean.TRUE),
                Arguments.of(createStandardTokenClaims(Set.of("s2.r2.a2")), Set.of(), Set.of(Permission.from("s1.r1.a1")), Boolean.FALSE),
                Arguments.of(createStandardTokenClaims(Set.of("s1.r1.a1")), Set.of(Permission.from("s1.r1.a1")), Set.of(), Boolean.TRUE),
                Arguments.of(createStandardTokenClaims(Set.of("s2.r2.a2")), Set.of(Permission.from("s1.r1.a1")), Set.of(), Boolean.FALSE)
        );
    }

    private static StandardTokenClaims createStandardTokenClaims(Set<String> scope) throws URISyntaxException {
        return new StandardTokenClaims("k001",  "iss", "sub", Set.of(), scope,
                OrganizationId.from("o-001"), ProjectId.from("p-001"), TokenType.BEARER);
    }

    @ParameterizedTest
    @MethodSource("provideValidatePermissionsData")
    void testValidatePermissions(StandardTokenClaims standardTokenClaims, Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions, Boolean expectedResult) {
        boolean result = JWTUtils.validatePermissions(standardTokenClaims, requiredAdminPermissions, requiredApplicationPermissions);
        assertEquals(expectedResult, result);
    }

}
