package itx.iamservice.core.tests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.TokenUtils;
import itx.iamservice.core.services.dto.JWToken;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenUtilsTest {

    private static final String ISSUER = "issuer";
    private static final String AUDIENCE = "audience";
    private static final String SUBJECT = "subject";
    private static final OrganizationId ORGANIZATION_ID = OrganizationId.from(ISSUER);
    private static final ProjectId PROJECT_ID = ProjectId.from(AUDIENCE);
    private static final ClientId CLIENT_ID = ClientId.from(SUBJECT);
    private static final Set<String> ROLES = Set.of("role-a", "role-b", "role-c");
    private static final Long DURATION = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final Set<RoleId> availableRoles = Set.of(RoleId.from("role1"), RoleId.from("role2"), RoleId.from("role3"));

    @BeforeAll
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
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
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, PROJECT_ID, CLIENT_ID, DURATION, TIME_UNIT, ROLES, keyPair.getPrivate(), TokenType.BEARER);
        assertNotNull(jwt);
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair.getPublic());
        assertTrue(claimsJws.isPresent());
        assertEquals(SUBJECT, claimsJws.get().getBody().getSubject());
        assertEquals(ISSUER, claimsJws.get().getBody().getIssuer());
        assertEquals(AUDIENCE, claimsJws.get().getBody().getAudience());
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
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, PROJECT_ID, CLIENT_ID, duration, TIME_UNIT, ROLES, keyPair.getPrivate(), TokenType.BEARER);
        Thread.sleep(3*1000L);
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair.getPublic());
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    public void jwTokenInvalidKeyTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, PROJECT_ID, CLIENT_ID, DURATION, TIME_UNIT, ROLES, keyPair.getPrivate(), TokenType.BEARER);
        keyPair = TokenUtils.generateKeyPair();
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair.getPublic());
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    public void extractTokenTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, PROJECT_ID, CLIENT_ID, DURATION, TIME_UNIT, ROLES, keyPair.getPrivate(), TokenType.BEARER);
        DefaultClaims defaultClaims = TokenUtils.extractClaims(jwt);
        assertEquals(SUBJECT, defaultClaims.getSubject());
        assertEquals(ISSUER, defaultClaims.getIssuer());
        assertEquals(AUDIENCE, defaultClaims.getAudience());
    }

    @Test
    public void identicalTokenGenerationTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        Date issuedAt = new Date();
        Date notBefore = issuedAt;
        Date expirationTime = new Date(issuedAt.getTime() + 3600*1000L);
        JWToken jwt1 = TokenUtils.issueToken(SUBJECT, ISSUER, AUDIENCE, expirationTime, notBefore, issuedAt, ROLES, keyPair.getPrivate(), TokenType.ID);
        JWToken jwt2 = TokenUtils.issueToken(SUBJECT, ISSUER, AUDIENCE, expirationTime, notBefore, issuedAt, ROLES, keyPair.getPrivate(), TokenType.ID);
        assertFalse(jwt1.equals(jwt2));
    }

    @Test
    public void signedCertificateHierarchyTest() throws PKIException {
        String organizationId = "organization-001";
        String projectId = "project-001";
        String clientId = "client-001";
        KeyPairData organizationKeyPairData = TokenUtils.createSelfSignedKeyPairData(organizationId, 10L, TimeUnit.DAYS);
        KeyPairData projectKeyPairData = TokenUtils.createSignedKeyPairData(organizationId, projectId, 10L, TimeUnit.DAYS, organizationKeyPairData.getPrivateKey());
        KeyPairData clientKeyPairData = TokenUtils.createSignedKeyPairData(projectId, clientId, 10L, TimeUnit.DAYS, projectKeyPairData.getPrivateKey());
        TokenUtils.verifySelfSignedCertificate(organizationKeyPairData.getX509Certificate());
        TokenUtils.verifySignedCertificate(organizationKeyPairData.getX509Certificate(), projectKeyPairData.getX509Certificate());
        TokenUtils.verifySignedCertificate(projectKeyPairData.getX509Certificate(), clientKeyPairData.getX509Certificate());
    }

    private static Stream<Arguments> createRoleFilterArguments() {
        return Stream.of(
                Arguments.of(availableRoles, Set.of(), availableRoles),
                Arguments.of(availableRoles, Set.of(RoleId.from("role1")), Set.of(RoleId.from("role1"))),
                Arguments.of(availableRoles, Set.of(RoleId.from("roleX")), Set.of()),
                Arguments.of(availableRoles, Set.of(RoleId.from("role1"), RoleId.from("roleX")), Set.of(RoleId.from("role1")))
        );
    }

    @ParameterizedTest
    @MethodSource("createRoleFilterArguments")
    public void roleFilteringTest(Set<RoleId> availableRoles, Set<RoleId> scope, Set<RoleId> expectedResult) {
        Set<RoleId> filterRoles = TokenUtils.filterRoles(availableRoles, scope);
        assertNotNull(filterRoles);
        assertTrue(expectedResult.size() == filterRoles.size());
        filterRoles.forEach(
                r -> assertTrue(expectedResult.contains(r))
        );
    }

}