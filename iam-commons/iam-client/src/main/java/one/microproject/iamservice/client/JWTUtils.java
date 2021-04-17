package one.microproject.iamservice.client;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.impl.DefaultClaims;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.client.impl.JWKSigningKeyResolver;
import one.microproject.iamservice.core.KeyProvider;
import one.microproject.iamservice.client.impl.ProviderSigningKeyResolver;
import one.microproject.iamservice.core.dto.JWKData;
import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;


public final class JWTUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JWTUtils.class);

    public static final String ALGORITHM   =  "RSA";
    public static final String BC_PROVIDER =  "BC";

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

    public static boolean validatePermissions(StandardTokenClaims tokenClaims, Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions) {
        Set<String> scopeSet = tokenClaims.getScope();
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

    public static Optional<StandardTokenClaims> validateToken(PublicKey key, JWToken token) {
        KeyProvider keyProvider = keyId -> key;
        return validateToken(keyProvider, token);
    }

    public static Optional<StandardTokenClaims> validateToken(KeyProvider keyProvider, JWToken token) {
        try {
            ProviderSigningKeyResolver providerSigningKeyResolver = new ProviderSigningKeyResolver(keyProvider);
            return Optional.of(getStandardTokenClaims(providerSigningKeyResolver, token));
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        return Optional.empty();
    }

    public static Optional<StandardTokenClaims> validateToken(OrganizationId organizationId, ProjectId projectId, JWKResponse response, JWToken token) {
        try {
            JWKSigningKeyResolver resolver = new JWKSigningKeyResolver(response);
            StandardTokenClaims claims = getStandardTokenClaims(resolver, token);
            if (organizationId.equals(claims.getOrganizationId()) && projectId.equals(claims.getProjectId())) {
                return Optional.of(claims);
            } else {
                LOG.warn("Invalid organization ID or project ID.");
            }
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        return Optional.empty();
    }

    public static boolean validateToken(OrganizationId organizationId, ProjectId projectId, JWKResponse response, Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions, JWToken token) {
        Optional<StandardTokenClaims> standardTokenClaims = validateToken(organizationId, projectId, response, token);
        if (standardTokenClaims.isPresent()){
            return validatePermissions(standardTokenClaims.get(), requiredAdminPermissions, requiredApplicationPermissions);
        }
        return false;
    }

    public static PublicKey createPublicKey(JWKData jwkData) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        BigInteger modulus = new BigInteger(Base64.getDecoder().decode(jwkData.getModulusValue()));
        BigInteger exponent = new BigInteger(Base64.getDecoder().decode(jwkData.getExponentValue()));
        KeyFactory factory = KeyFactory.getInstance(ALGORITHM, BC_PROVIDER);
        return factory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
    }

    private static StandardTokenClaims getStandardTokenClaims(SigningKeyResolver signingKeyResolver, JWToken token) throws URISyntaxException {
        LOG.debug("getStandardTokenClaims");
        Jwt jwt = Jwts.parserBuilder()
                .setSigningKeyResolver(signingKeyResolver)
                .build()
                .parse(token.getToken());
        String kid = (String)jwt.getHeader().get("kid");
        DefaultClaims claims = (DefaultClaims)jwt.getBody();
        String iss = claims.getIssuer();
        String sub = claims.getSubject();
        Set<String> aud = Set.of(claims.getAudience().split(" "));
        Set<String> scopes = Set.of(((String)claims.get(SCOPE)).split(" "));
        String[] split = iss.split("/");
        int organizationIndex = split.length - 2;
        int projectIndex = split.length - 1;
        OrganizationId issOrganizationId = OrganizationId.from(split[organizationIndex]);
        ProjectId issProjectId = ProjectId.from(split[projectIndex]);
        String tokenType = (String) claims.get("typ");
        LOG.debug("getStandardTokenClaims: OK");
        return new StandardTokenClaims(kid, iss, sub, aud, scopes, issOrganizationId, issProjectId, TokenType.getTokenType(tokenType));
    }

}
