package itx.iamservice.core.tests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.TokenUtils;
import itx.iamservice.core.services.dto.JWToken;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void keyPairGenerateTest() throws NoSuchAlgorithmException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        assertNotNull(keyPair);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void jwTokenValidityTest() throws NoSuchAlgorithmException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        assertNotNull(keyPair);
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, PROJECT_ID, CLIENT_ID, DURATION, TIME_UNIT, ROLES, keyPair);
        assertNotNull(jwt);
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair);
        assertTrue(claimsJws.isPresent());
        assertEquals(SUBJECT, claimsJws.get().getBody().getSubject());
        assertEquals(ISSUER, claimsJws.get().getBody().getIssuer());
        assertEquals(AUDIENCE, claimsJws.get().getBody().getAudience());
        List<String> claimedRoles = (List<String>) claimsJws.get().getBody().get(TokenUtils.ROLES_CLAIM);
        assertNotNull(claimedRoles);
        assertEquals(ROLES.size(), claimedRoles.size());
        for (String role : claimedRoles) {
            assertTrue(ROLES.contains(role));
        }
    }

    @Test
    public void jwTokenExpiredTest() throws NoSuchAlgorithmException, InterruptedException {
        Long duration = 1L;
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, PROJECT_ID, CLIENT_ID, duration, TIME_UNIT, ROLES, keyPair);
        Thread.sleep(3*1000L);
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair);
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    public void jwTokenInvalidKeyTest() throws NoSuchAlgorithmException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, PROJECT_ID, CLIENT_ID, DURATION, TIME_UNIT, ROLES, keyPair);
        keyPair = TokenUtils.generateKeyPair();
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair);
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    public void extractTokenTest() throws NoSuchAlgorithmException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(ORGANIZATION_ID, PROJECT_ID, CLIENT_ID, DURATION, TIME_UNIT, ROLES, keyPair);
        DefaultClaims defaultClaims = TokenUtils.extractClaims(jwt);
        assertEquals(SUBJECT, defaultClaims.getSubject());
        assertEquals(ISSUER, defaultClaims.getIssuer());
        assertEquals(AUDIENCE, defaultClaims.getAudience());
    }

    @Test
    public void identicalTokenGenerationTest() throws NoSuchAlgorithmException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        Date issuedAt = new Date();
        Date notBefore = issuedAt;
        Date expirationTime = new Date(issuedAt.getTime() + 3600*1000L);
        JWToken jwt1 = TokenUtils.issueToken(SUBJECT, ISSUER, AUDIENCE, expirationTime, notBefore, issuedAt, ROLES, keyPair);
        JWToken jwt2 = TokenUtils.issueToken(SUBJECT, ISSUER, AUDIENCE, expirationTime, notBefore, issuedAt, ROLES, keyPair);
        assertFalse(jwt1.equals(jwt2));
    }

}