package itx.iamservice.core.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.services.dto.JWToken;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class TokenUtils {

    public static final String ROLES_CLAIM = "roles";

    private TokenUtils() {
    }

    public static JWToken issueToken(String subject, String issuer, Long duration, TimeUnit timeUnit, String audience, Set<String> roles, KeyPair keyPair) {
        Date issuedAt = new Date();
        Date notBefore = issuedAt;
        Date expirationTime = new Date(issuedAt.getTime() + timeUnit.toMillis(duration));
        return issueToken(subject, issuer, audience, expirationTime, notBefore, issuedAt, roles, keyPair);
    }

    public static JWToken issueToken(String subject, String issuer, String audience, Date expirationTime, Date notBefore, Date issuedAt, Set<String> roles, KeyPair keyPair) {
        String jwToken = Jwts.builder()
                .setSubject(subject)
                .signWith(keyPair.getPrivate())
                .setExpiration(expirationTime)
                .setIssuer(issuer)
                .setIssuedAt(issuedAt)
                .setNotBefore(notBefore)
                .setAudience(audience)
                .claim(ROLES_CLAIM, roles)
                .compact();
        return JWToken.from(jwToken);
    }

    public static Optional<Jws<Claims>> verify(JWToken jwToken, KeyPair keyPair) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(keyPair.getPrivate()).build().parseClaimsJws(jwToken.getToken());
            return Optional.of(claimsJws);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG");
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }

    @SuppressWarnings("unchecked")
    public static DefaultClaims extractClaims(JWToken jwToken) {
        String token = jwToken.getToken();
        String jwTokenWithoutSignature = token.substring(0, token.lastIndexOf('.') + 1);
        Jwt jwt = Jwts.parserBuilder().build().parse(jwTokenWithoutSignature);
        return (DefaultClaims) jwt.getBody();
    }

}
