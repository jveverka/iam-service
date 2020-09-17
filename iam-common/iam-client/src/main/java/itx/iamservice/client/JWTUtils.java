package itx.iamservice.client;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import itx.iamservice.client.dto.StandardTokenClaims;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public final class JWTUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JWTUtils.class);

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String SCOPE = "scope";

    private JWTUtils() {
        throw new UnsupportedOperationException("Do not instantiate utility class.");
    }

    public static String createAuthorizationHeader(String jwToken) {
        return BEARER_PREFIX + jwToken.trim();
    }

    public static JWToken extractJwtToken(String authorization) {
        return new JWToken(authorization.substring(BEARER_PREFIX.length(), authorization.length()).trim());
    }

    public static boolean validateToken(java.security.interfaces.RSAKey rsaKey, URI issuerUri, ProjectId projectId, Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions, JWToken token) {
        String issuerValue = issuerUri.toString();
        Optional<JWTClaimsSet> claimSet = validateToken(rsaKey, projectId, issuerValue, token);
        if (claimSet.isPresent()) {
            String scopeClaim = (String) claimSet.get().getClaim(JWTUtils.SCOPE);
            String[] scopes = scopeClaim.trim().split(" ");
            Set<String> scopeSet = new HashSet<>(Arrays.asList(scopes));
            if (!requiredAdminPermissions.isEmpty()) {
                for (Permission scope : requiredAdminPermissions) {
                    if (!scopeSet.contains(scope.asStringValue())) {
                        return false;
                    }
                }
                return true;
            } else {
                for (Permission scope : requiredApplicationPermissions) {
                    if (!scopeSet.contains(scope.asStringValue())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static Optional<JWTClaimsSet> validateToken(java.security.interfaces.RSAKey rsaKey, ProjectId projectId, String issuerValue, JWToken token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token.getToken());
            RSASSAVerifier rsassaVerifier = new RSASSAVerifier(convert(rsaKey));
            if (signedJWT.verify(rsassaVerifier) &&
                issuerValue.equals(signedJWT.getJWTClaimsSet().getIssuer()) &&
                signedJWT.getJWTClaimsSet().getAudience().contains(projectId.getId())) {
                return Optional.of(signedJWT.getJWTClaimsSet());
            }
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        return Optional.empty();
    }

    public static Optional<StandardTokenClaims> getClaimsFromToken(JWToken token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token.getToken());
            String kid = signedJWT.getHeader().getKeyID();
            String iss = signedJWT.getJWTClaimsSet().getIssuer();
            String sub = signedJWT.getJWTClaimsSet().getSubject();
            List<String> aud = signedJWT.getJWTClaimsSet().getAudience();
            String[] split = iss.split("/");
            int organizationIndex = split.length - 2;
            int projectIndex = split.length - 1;
            return Optional.of(new StandardTokenClaims(kid, iss, sub, aud,
                    OrganizationId.from(split[organizationIndex]),
                    ProjectId.from(split[projectIndex])));
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        return Optional.empty();
    }

    public static java.security.interfaces.RSAKey convert(RSAKey rsaKey) throws JOSEException {
        return rsaKey.toRSAPublicKey();
    }

    public static RSAKey convert(java.security.interfaces.RSAKey rsaKey) {
        return new RSAKey.Builder((RSAPublicKey) rsaKey)
                .build();
    }

}
