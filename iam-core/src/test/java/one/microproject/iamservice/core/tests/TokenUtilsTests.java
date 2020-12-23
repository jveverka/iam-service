package one.microproject.iamservice.core.tests;

import io.jsonwebtoken.impl.DefaultClaims;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.KeyId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.KeyPairData;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.utils.TokenUtils;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.services.dto.Scope;
import one.microproject.iamservice.client.impl.TokenValidatorImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
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
    private static final ProjectId PROJECT_ID = ProjectId.from(ISSUER);
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
    private static final TokenValidator tokenValidator =  new TokenValidatorImpl();

    private static URI issuerUri;
    private static String issuerClaim;

    @BeforeAll
    private static void init() throws URISyntaxException {
        Security.addProvider(new BouncyCastleProvider());
        issuerUri = new URI("http://localhost:8080/" + ISSUER + "/" + ISSUER);
        issuerClaim = issuerUri.toString();
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
        JWToken jwt = TokenUtils.issueToken(issuerUri, ORGANIZATION_ID, PROJECT_ID, AUDIENCE, USER_ID, DURATION, TIME_UNIT, new Scope(ROLES), claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.BEARER);
        assertNotNull(jwt);
        Optional<StandardTokenClaims> tokenClaimsOptional = tokenValidator.validateToken(keyPair.getPublic(), jwt);
        assertTrue(tokenClaimsOptional.isPresent());
        StandardTokenClaims tokenClaims = tokenClaimsOptional.get();
        assertEquals(KEY_ID.getId(), tokenClaims.getKeyId());
        assertEquals(SUBJECT, tokenClaims.getSubject());
        assertEquals(issuerClaim, tokenClaims.getIssuer());
        assertNotNull(tokenClaims.getAudience());
        assertNotNull(tokenClaims.getScope());
        assertEquals(TokenType.BEARER, tokenClaims.getType());
        assertEquals(ROLES.size(), tokenClaims.getScope().size());
        for (String role : tokenClaims.getScope()) {
            assertTrue(ROLES.contains(role));
        }
    }

    @Test
    public void jwTokenExpiredTest() throws NoSuchAlgorithmException, InterruptedException, NoSuchProviderException {
        Long duration = 1L;
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(issuerUri, ORGANIZATION_ID, PROJECT_ID, AUDIENCE, USER_ID, duration, TIME_UNIT, Scope.empty(), claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.BEARER);
        Thread.sleep(3*1000L);
        Optional<StandardTokenClaims> claimsJws = tokenValidator.validateToken(keyPair.getPublic(), jwt);
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    public void jwTokenInvalidKeyTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(issuerUri, ORGANIZATION_ID, PROJECT_ID, AUDIENCE, USER_ID, DURATION, TIME_UNIT, Scope.empty(), claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.BEARER);
        keyPair = TokenUtils.generateKeyPair();
        Optional<StandardTokenClaims> claimsJws = tokenValidator.validateToken(keyPair.getPublic(), jwt);
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void extractTokenTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(issuerUri, ORGANIZATION_ID, PROJECT_ID, AUDIENCE, USER_ID, DURATION, TIME_UNIT, Scope.empty(), claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.BEARER);
        DefaultClaims defaultClaims = TokenUtils.extractClaims(jwt);
        assertEquals(SUBJECT, defaultClaims.getSubject());
        assertEquals(issuerClaim, defaultClaims.getIssuer());
        List<String> claimedAudience = (List<String>) defaultClaims.get(TokenUtils.AUDIENCE_CLAIM);
        assertNotNull(claimedAudience);
    }

    @Test
    public void identicalTokenGenerationTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        Date issuedAt = new Date();
        Date notBefore = issuedAt;
        Date expirationTime = new Date(issuedAt.getTime() + 3600*1000L);
        JWToken jwt1 = TokenUtils.issueToken(issuerUri, ORGANIZATION_ID, PROJECT_ID, SUBJECT, AUDIENCE, expirationTime, notBefore, issuedAt, Scope.empty(), claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.ID);
        JWToken jwt2 = TokenUtils.issueToken(issuerUri, ORGANIZATION_ID, PROJECT_ID, SUBJECT, AUDIENCE, expirationTime, notBefore, issuedAt, Scope.empty(), claimRoles, KEY_ID, keyPair.getPrivate(), TokenType.ID);
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
                Arguments.of(availablePermissions, new Scope(Set.of()), new Scope(Set.of("service.resource1.all", "service.resource2.all", "service.resource3.all"))),
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
    public void scopeParsingTest(String scopes, Scope expectedResult) {
        Scope result = ModelUtils.getScopes(scopes);
        assertNotNull(result);
        assertEquals(result, expectedResult);
    }

    @Test
    public void testBigIntegerConversion() {
        BigInteger bigInteger = BigInteger.valueOf(System.currentTimeMillis());
        byte[] bytes = TokenUtils.toBytesUnsigned(bigInteger);
        BigInteger deserialized =  new BigInteger(bytes);
        assertEquals(bigInteger, deserialized);
    }

    @Test
    public void testPublicKeySerializationAndDeserialization() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        BigInteger exponent = rsaPublicKey.getPublicExponent();
        BigInteger modulus = rsaPublicKey.getModulus();
        byte[] exponentBytes = TokenUtils.toBytesUnsigned(exponent);
        byte[] modulusBytes = TokenUtils.toBytesUnsigned(modulus);

        BigInteger modulusDecoded = new BigInteger(modulusBytes);
        BigInteger exponentDecoded = new BigInteger(exponentBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        PublicKey generated = keyFactory.generatePublic(new RSAPublicKeySpec(modulusDecoded, exponentDecoded));
        assertNotNull(generated);
        assertEquals(keyPair.getPublic(), generated);
    }

}