package itx.iamservice.core.model.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.KeyId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.IdTokenRequest;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.dto.Scope;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class TokenUtils {

    private static final String BC_PROVIDER = "BC";
    private static final String SHA256_RSA = "SHA256withRSA";
    private static final String CN_DIR_NAME = "CN=";
    private static final String X509_TYPE = "X.509";
    private static final String ALGORITHM = "RSA";

    public static final String ROLES_CLAIM = "roles";
    public static final String PERMISSIONS_CLAIM = "permissions";
    public static final String TYPE_CLAIM = "typ";
    public static final String NONCE_CLAIM = "nonce";
    public static final String AUDIENCE_CLAIM = "aud";
    public static final String AUTH_TIME_CLAIM = "auth_time";
    public static final String SCOPE_CLAIM = "scope";
    public static final String KEY_ID = "kid";
    public static final String TYP_ID = "typ";
    public static final String TYP_VALUE = "JWT";

    private TokenUtils() {
    }

    /**
     * Filter permissions by scope. If scope is empty, return all available permissions. If scope contains some permissions that
     * are not present in availablePermissions set, those are ignored.
     * @param availablePermissions all available permissions.
     * @param scope subset of available permissions.
     * @return permissions set filtered by scope.
     */
    public static Scope filterScopes(Set<Permission> availablePermissions, Scope scope) {
        if (scope.isEmpty()) {
            Set<String> scopes = availablePermissions.stream().map(p->p.asStringValue()).collect(Collectors.toSet());
            return new Scope(scopes);
        } else {
            Set<String> scopes = availablePermissions.stream().filter(p -> scope.getValues().contains(p.asStringValue())).map(p->p.asStringValue()).collect(Collectors.toSet());
            return new Scope(scopes);
        }
    }

    public static Map<String, Set<String>> getPermissionsClaims(Set<Permission> permissions) {
        Map<String, Set<String>> claims = new HashMap<>();
        Set<String> values = permissions.stream().map(p->p.asStringValue()).collect(Collectors.toSet());
        claims.put(PERMISSIONS_CLAIM, values);
        return claims;
    }

    public static JWToken issueToken(URI issuerUri, OrganizationId organizationId, ProjectId projectId, Set<String> audience, ClientId clientId, Long duration, TimeUnit timeUnit, Scope scope, Map<String, Set<String>> customClaims, KeyId keyId, PrivateKey privateKey, TokenType type) {
        return issueToken(issuerUri, organizationId, projectId, audience, clientId.getId(), duration, timeUnit, scope, customClaims, keyId, privateKey, type);
    }

    public static JWToken issueToken(URI issuerUri, OrganizationId organizationId, ProjectId projectId, Set<String> audience, UserId userId, Long duration, TimeUnit timeUnit, Scope scope, Map<String, Set<String>> customClaims, KeyId keyId, PrivateKey privateKey, TokenType type) {
        return issueToken(issuerUri, organizationId, projectId, audience, userId.getId(), duration, timeUnit, scope, customClaims, keyId, privateKey, type);
    }

    public static JWToken issueToken(URI issuerUri, OrganizationId organizationId, ProjectId projectId, Set<String> audience, String subject, Long duration, TimeUnit timeUnit, Scope scope, Map<String, Set<String>> customClaims, KeyId keyId, PrivateKey privateKey, TokenType type) {
        Date issuedAt = new Date();
        Date notBefore = issuedAt;
        Date expirationTime = new Date(issuedAt.getTime() + timeUnit.toMillis(duration));
        return issueToken(issuerUri, organizationId, projectId, subject, audience, expirationTime, notBefore, issuedAt, scope, customClaims, keyId, privateKey, type);
    }

    public static JWToken issueIdToken(URI issuerUri, OrganizationId organizationId, ProjectId projectId, ClientId clientId, String clientOrUserId, Long duration, TimeUnit timeUnit, IdTokenRequest idTokenRequest, KeyId keyId, PrivateKey privateKey) {
        String subject = organizationId.getId() + "/" + projectId.getId() + "/" + clientOrUserId;
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + timeUnit.toMillis(duration));
        JwtBuilder builder = Jwts.builder();
        builder.setHeaderParam(TYP_ID, TYP_VALUE);
        builder.setHeaderParam(KEY_ID, keyId.getId());
        builder.setIssuer(issuerUri.toString());
        builder.setSubject(subject);
        builder.setAudience(clientId.getId());
        builder.setExpiration(expiration);
        builder.setIssuedAt(issuedAt);
        builder.claim(AUTH_TIME_CLAIM, issuedAt.getTime());
        if (idTokenRequest.getNonce() != null) {
            builder.claim(NONCE_CLAIM, idTokenRequest.getNonce());
        }
        builder.signWith(privateKey);
        return JWToken.from(builder.compact());
    }

    public static JWToken issueToken(URI issuerUri, OrganizationId organizationId, ProjectId projectId, String subject, Set<String> audience, Date expirationTime, Date notBefore, Date issuedAt, Scope scope, Map<String, Set<String>> customClaims, KeyId keyId, PrivateKey privateKey, TokenType type) {
        JwtBuilder builder = Jwts.builder();
        builder.setHeaderParam(TYP_ID, TYP_VALUE);
        builder.setHeaderParam(KEY_ID, keyId.getId());
        builder.setSubject(subject);
        builder.signWith(privateKey);
        builder.setExpiration(expirationTime);
        builder.setIssuer(issuerUri.toString());
        builder.setIssuedAt(issuedAt);
        builder.setNotBefore(notBefore);
        builder.claim(AUDIENCE_CLAIM, audience);
        builder.claim(TYPE_CLAIM, type.getType());
        builder.claim(SCOPE_CLAIM, String.join(" ", scope.getValues()));
        builder.setId(UUID.randomUUID().toString());
        if (customClaims != null) {
            customClaims.forEach((k, v) -> builder.claim(k, v));
        }
        return JWToken.from(builder.compact());
    }

    public static Optional<Jws<Claims>> verify(JWToken jwToken, PublicKey publicKey) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(jwToken.getToken());
            return Optional.of(claimsJws);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, BC_PROVIDER);
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

    public static X509Certificate createSignedCertificate(String issuerName, String subjectName, Long duration, TimeUnit timeUnit, PublicKey publicKey, PrivateKey privateKey) throws OperatorCreationException, IOException, CertificateException, NoSuchProviderException {
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + timeUnit.toMillis(duration));
        return createSignedCertificate(issuerName, subjectName, notBefore, notAfter, publicKey, privateKey);
    }

    public static X509Certificate createSignedCertificate(String issuerName, String subjectName, Date notBefore, Date notAfter, PublicKey publicKey, PrivateKey privateKey) throws OperatorCreationException, IOException, CertificateException, NoSuchProviderException {
        X500Name issuer = new X500Name(CN_DIR_NAME + issuerName);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        X500Name subject = new X500Name(CN_DIR_NAME + subjectName);
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKeyInfo);
        JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder(SHA256_RSA);
        ContentSigner signer = jcaContentSignerBuilder.build(privateKey);
        CertificateFactory certificateFactory = CertificateFactory.getInstance(X509_TYPE, BC_PROVIDER);
        byte[] certBytes = certBuilder.build(signer).getEncoded();
        return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
    }

    public static X509Certificate createSelfSignedCertificate(String issuerAndSubject, Date notBefore, Date notAfter, KeyPair keyPair) throws OperatorCreationException, IOException, CertificateException, NoSuchProviderException {
        return createSignedCertificate(issuerAndSubject, issuerAndSubject, notBefore, notAfter, keyPair.getPublic(), keyPair.getPrivate());
    }

    public static X509Certificate createSelfSignedCertificate(String issuerAndSubject, Long duration, TimeUnit timeUnit, KeyPair keyPair) throws OperatorCreationException, IOException, CertificateException, NoSuchProviderException {
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + timeUnit.toMillis(duration));
        return createSelfSignedCertificate(issuerAndSubject, notBefore, notAfter, keyPair);
    }

    public static void verifyCertificate(X509Certificate caCertificate, X509Certificate userCertificate) throws CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        caCertificate.checkValidity();
        userCertificate.checkValidity();
        userCertificate.verify(caCertificate.getPublicKey());
    }

    public static KeyPairData createSelfSignedKeyPairData(String issuerAndSubject, Long duration, TimeUnit timeUnit) throws PKIException {
        try {
            KeyId keyId = KeyId.from(UUID.randomUUID().toString());
            KeyPair keyPair = generateKeyPair();
            X509Certificate x509Certificate = createSelfSignedCertificate(issuerAndSubject, duration, timeUnit, keyPair);
            return new KeyPairData(keyId, keyPair.getPrivate(), x509Certificate);
        } catch (Exception e) {
            throw new PKIException(e);
        }
    }

    public static KeyPairData createSignedKeyPairData(String issuer, String subject, Long duration, TimeUnit timeUnit, PrivateKey privateKey) throws PKIException {
        try {
            KeyId keyId = KeyId.from(UUID.randomUUID().toString());
            KeyPair keyPair = generateKeyPair();
            X509Certificate x509Certificate = createSignedCertificate(issuer, subject, duration, timeUnit, keyPair.getPublic(), privateKey);
            return new KeyPairData(keyId, keyPair.getPrivate(), x509Certificate);
        } catch (Exception e) {
            throw new PKIException(e);
        }
    }

    public static void verifySignedCertificate(X509Certificate issuerCertificate, X509Certificate signedCertificate) throws PKIException {
        try {
            verifyCertificate(issuerCertificate, signedCertificate);
        } catch (Exception e) {
            throw new PKIException(e);
        }
    }

    public static void verifySelfSignedCertificate(X509Certificate selfSignedCertificate) throws PKIException {
        try {
            verifyCertificate(selfSignedCertificate, selfSignedCertificate);
        } catch (Exception e) {
            throw new PKIException(e);
        }
    }

    public static String serializeX509Certificate(X509Certificate certificate) throws PKIException {
        try {
            return Base64.getEncoder().encodeToString(certificate.getEncoded());
        } catch(Exception e) {
            throw new PKIException(e);
        }
    }

    public static X509Certificate deserializeX509Certificate(String base64EncodedCertificate) throws PKIException {
        try {
            byte[] data = Base64.getDecoder().decode(base64EncodedCertificate);
            CertificateFactory certificateFactory = CertificateFactory.getInstance(X509_TYPE, BC_PROVIDER);
            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(data));
        } catch(Exception e) {
            throw new PKIException(e);
        }
    }

    public static String serializePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PrivateKey deserializePrivateKey(String base64EncodedCertificate) throws PKIException {
        try {
            byte[] data = Base64.getDecoder().decode(base64EncodedCertificate);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, BC_PROVIDER);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(data);
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch(Exception e) {
            throw new PKIException(e);
        }
    }

    /**
     * Returns a byte array representation of the specified big integer
     * without the sign bit.
     *
     * @param bigInt The big integer to be converted. Must not be
     *               {@code null}.
     *
     * @return A byte array representation of the big integer, without the
     *         sign bit.
     */
    public static byte[] toBytesUnsigned(final BigInteger bigInt) {
        return bigInt.toByteArray();
        /**
        // Copied from Apache Commons Codec 1.8
        int bitlen = bigInt.bitLength();
        // round bitlen
        bitlen = ((bitlen + 7) >> 3) << 3;
        final byte[] bigBytes = bigInt.toByteArray();
        if (((bigInt.bitLength() % 8) != 0) && (((bigInt.bitLength() / 8) + 1) == (bitlen / 8))) {
            return bigBytes;
        }
        // set up params for copying everything but sign bit
        int startSrc = 0;
        int len = bigBytes.length;

        // if bigInt is exactly byte-aligned, just skip signbit in copy
        if ((bigInt.bitLength() % 8) == 0) {
            startSrc = 1;
            len--;
        }

        final int startDst = bitlen / 8 - len; // to pad w/ nulls as per spec
        final byte[] resizedBytes = new byte[bitlen / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
        return resizedBytes;
        */
    }

}
