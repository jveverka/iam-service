package itx.iamservice.core.tests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.KeyId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.utils.TokenUtils;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.dto.Scope;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenUtilsTests {

    private static final String ISSUER = "issuer";
    private static final String SUBJECT = "subject";
    private static final OrganizationId ORGANIZATION_ID = OrganizationId.from(ISSUER);
    private static final Set<String> AUDIENCE = Set.of("audience");
    private static final UserId USER_ID = UserId.from(SUBJECT);
    private static final Set<String> ROLES = Set.of("role-a", "role-b", "role-c");
    private static final Long DURATION = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final Set<Permission> availablePermissions = Set.of(
            new Permission("service", "resource1", "all"),
            new Permission("service", "resource2", "all"),
            new Permission("service", "resource3", "all")
            );
    private static final KeyId KEY_ID = KeyId.from("key-001");
    private static final Map<String, Set<String>> claimRoles = new HashMap<>();
    private static final Scope scope = Scope.empty();

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
        claimRoles.put(TokenUtils.ROLES_CLAIM, ROLES);
    }

    @Test
    public void keyPairGenerateTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        assertNotNull(keyPair);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void jwTokenValidityTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        assertNotNull(keyPair);
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, AUDIENCE, USER_ID, DURATION, TIME_UNIT, scope, claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.BEARER);
        assertNotNull(jwt);
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair.getPublic());
        assertTrue(claimsJws.isPresent());
        assertEquals(KEY_ID.getId(), claimsJws.get().getHeader().getKeyId());
        assertEquals(SUBJECT, claimsJws.get().getBody().getSubject());
        assertEquals(ISSUER, claimsJws.get().getBody().getIssuer());
        List<String> claimedAudience = (List<String>) claimsJws.get().getBody().get(TokenUtils.AUDIENCE_CLAIM);
        assertNotNull(claimedAudience);
        List<String> claimedRoles = (List<String>) claimsJws.get().getBody().get(TokenUtils.ROLES_CLAIM);
        assertNotNull(claimedRoles);
        String type = (String)claimsJws.get().getBody().get(TokenUtils.TYPE_CLAIM);
        assertEquals(TokenType.BEARER.getType(), type);
        assertEquals(ROLES.size(), claimedRoles.size());
        for (String role : claimedRoles) {
            assertTrue(ROLES.contains(role));
        }
    }

    @Test
    public void jwTokenExpiredTest() throws NoSuchAlgorithmException, InterruptedException, NoSuchProviderException {
        Long duration = 1L;
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, AUDIENCE, USER_ID, duration, TIME_UNIT, scope, claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.BEARER);
        Thread.sleep(3*1000L);
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair.getPublic());
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    public void jwTokenInvalidKeyTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, AUDIENCE, USER_ID, DURATION, TIME_UNIT, scope, claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.BEARER);
        keyPair = TokenUtils.generateKeyPair();
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair.getPublic());
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void extractTokenTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, AUDIENCE, USER_ID, DURATION, TIME_UNIT, scope, claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.BEARER);
        DefaultClaims defaultClaims = TokenUtils.extractClaims(jwt);
        assertEquals(SUBJECT, defaultClaims.getSubject());
        assertEquals(ISSUER, defaultClaims.getIssuer());
        List<String> claimedAudience = (List<String>) defaultClaims.get(TokenUtils.AUDIENCE_CLAIM);
        assertNotNull(claimedAudience);
    }

    @Test
    public void identicalTokenGenerationTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        Date issuedAt = new Date();
        Date notBefore = issuedAt;
        Date expirationTime = new Date(issuedAt.getTime() + 3600*1000L);
        JWToken jwt1 = TokenUtils.issueToken(SUBJECT, ISSUER, AUDIENCE, expirationTime, notBefore, issuedAt, scope, claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.ID);
        JWToken jwt2 = TokenUtils.issueToken(SUBJECT, ISSUER, AUDIENCE, expirationTime, notBefore, issuedAt, scope, claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.ID);
        assertFalse(jwt1.equals(jwt2));
    }

    @Test
    public void signedCertificateHierarchyTest() throws PKIException {
        String organizationId = "organization-001";
        String projectId = "project-001";
        String userId = "user-001";
        KeyPairData organizationKeyPairData = TokenUtils.createSelfSignedKeyPairData(organizationId, 10L, TimeUnit.DAYS);
        KeyPairData projectKeyPairData = TokenUtils.createSignedKeyPairData(organizationId, projectId, 10L, TimeUnit.DAYS, organizationKeyPairData.getPrivateKey());
        KeyPairData userKeyPairData = TokenUtils.createSignedKeyPairData(projectId, userId, 10L, TimeUnit.DAYS, projectKeyPairData.getPrivateKey());
        TokenUtils.verifySelfSignedCertificate(organizationKeyPairData.getX509Certificate());
        TokenUtils.verifySignedCertificate(organizationKeyPairData.getX509Certificate(), projectKeyPairData.getX509Certificate());
        TokenUtils.verifySignedCertificate(projectKeyPairData.getX509Certificate(), userKeyPairData.getX509Certificate());
    }

    private static Stream<Arguments> createRoleFilterArguments() {
        return Stream.of(
                Arguments.of(availablePermissions, Set.of(), availablePermissions),
                Arguments.of(availablePermissions, new Scope(Set.of("service.resource1.all")), new Scope(Set.of("service.resource1.all"))),
                Arguments.of(availablePermissions, new Scope(Set.of("roleX")), new Scope(Set.of())),
                Arguments.of(availablePermissions, new Scope(Set.of("service.resource1.all", "roleX")), new Scope(Set.of("service.resource1.all")))
        );
    }

    @ParameterizedTest
    @MethodSource("createRoleFilterArguments")
    public void roleFilteringTest(Set<Permission> availablePermissions, Scope scope, Scope expectedResult) {
        Scope filterScopes = TokenUtils.filterScopes(availablePermissions, scope);
        assertNotNull(filterScopes);
        assertTrue(expectedResult.getValues().size() == filterScopes.getValues().size());
        filterScopes.getValues().forEach(
                r -> assertTrue(expectedResult.getValues().contains(r))
        );
    }

    private static Stream<Arguments> createScopeArguments() {
        return Stream.of(
                Arguments.of(null, new Scope(Set.of())),
                Arguments.of("", new Scope(Set.of())),
                Arguments.of(" ", new Scope(Set.of())),
                Arguments.of("   ", new Scope(Set.of())),
                Arguments.of("service.resource1.all", new Scope(Set.of("service.resource1.all"))),
                Arguments.of(" roleX ", new Scope(Set.of("roleX"))),
                Arguments.of("service.resource1.all roleX", new Scope(Set.of("service.resource1.all", "roleX")))
        );
    }

    @ParameterizedTest
    @MethodSource("createScopeArguments")
    public void scopeParsingTest(String scopes, Set<RoleId> expectedResult) {
        Scope result = ModelUtils.getScopes(scopes);
        assertNotNull(result);
        assertEquals(result, expectedResult);
    }

}