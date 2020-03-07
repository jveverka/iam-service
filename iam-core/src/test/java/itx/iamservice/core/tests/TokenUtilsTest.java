package itx.iamservice.core.tests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.TokenUtils;
import itx.iamservice.core.services.dto.JWToken;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenUtilsTest {

    private static final String SUBJECT = "subject";
    private static final String ISSUER = "issuer";
    private static final String AUDIENCE = "audience";
    private static final Set<String> ROLES = Set.of("role-a", "role-b", "role-c");

    @Test
    public void keyPairGenerateTest() throws NoSuchAlgorithmException {
        KeyPair keyPair = TokenUtils.generateKeyPair();
        assertNotNull(keyPair);
    }

    @Test
    public void jwTokenValidityTest() throws NoSuchAlgorithmException {
        Long duration = 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        KeyPair keyPair = TokenUtils.generateKeyPair();
        assertNotNull(keyPair);
        JWToken jwt = TokenUtils.issueToken(SUBJECT, ISSUER, duration, timeUnit, AUDIENCE, ROLES, keyPair);
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
        TimeUnit timeUnit = TimeUnit.SECONDS;
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(SUBJECT, ISSUER, duration, timeUnit, AUDIENCE, ROLES, keyPair);
        Thread.sleep(3*1000L);
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair);
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    public void jwTokenInvalidKeyTest() throws NoSuchAlgorithmException {
        Long duration = 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(SUBJECT, ISSUER, duration, timeUnit, AUDIENCE, ROLES, keyPair);
        keyPair = TokenUtils.generateKeyPair();
        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(jwt, keyPair);
        assertTrue(claimsJws.isEmpty());
    }

    @Test
    public void extractTokenTest() throws NoSuchAlgorithmException {
        Long duration = 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        KeyPair keyPair = TokenUtils.generateKeyPair();
        JWToken jwt = TokenUtils.issueToken(SUBJECT, ISSUER, duration, timeUnit, AUDIENCE, ROLES, keyPair);
        DefaultClaims defaultClaims = TokenUtils.extractClaims(jwt);
        assertEquals(SUBJECT, defaultClaims.getSubject());
        assertEquals(ISSUER, defaultClaims.getIssuer());
        assertEquals(AUDIENCE, defaultClaims.getAudience());
    }

}