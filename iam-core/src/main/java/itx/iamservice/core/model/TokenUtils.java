package itx.iamservice.core.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.services.dto.JWToken;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
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
import java.util.Date;
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

    public static final String ROLES_CLAIM = "roles";
    public static final String TYPE_CLAIM = "typ";

    private TokenUtils() {
    }

    /**
     * Filter roles by scope. If scope is empty, return all available roles. If scope contains some roles that
     * are not present in availableRoles set, those are ignored.
     * @param availableRoles all available roles.
     * @param scope subset of available roles.
     * @return role set filtered by scope.
     */
    public static Set<RoleId> filterRoles(Set<RoleId> availableRoles, Set<RoleId> scope) {
        if (scope.isEmpty()) {
            return availableRoles;
        } else {
            return availableRoles.stream().filter(s -> scope.contains(s)).collect(Collectors.toSet());
        }
    }

    public static JWToken issueToken(OrganizationId organizationId, ProjectId projectId, ClientId clientId, Long duration, TimeUnit timeUnit, Set<String> roles, PrivateKey privateKey, TokenType type) {
        Date issuedAt = new Date();
        Date notBefore = issuedAt;
        Date expirationTime = new Date(issuedAt.getTime() + timeUnit.toMillis(duration));
        return issueToken(clientId.getId(), organizationId.getId(), projectId.getId(), expirationTime, notBefore, issuedAt, roles, privateKey, type);
    }

    public static JWToken issueToken(String subject, String issuer, String audience, Date expirationTime, Date notBefore, Date issuedAt, Set<String> roles, PrivateKey privateKey, TokenType type) {
        String jwToken = Jwts.builder()
                .setSubject(subject)
                .signWith(privateKey)
                .setExpiration(expirationTime)
                .setIssuer(issuer)
                .setIssuedAt(issuedAt)
                .setNotBefore(notBefore)
                .setAudience(audience)
                .claim(ROLES_CLAIM, roles)
                .claim(TYPE_CLAIM, type.getType())
                .setId(UUID.randomUUID().toString())
                .compact();
        return JWToken.from(jwToken);
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
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BC_PROVIDER);
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

    public static void verifyCertificate(X509Certificate caCertificate, X509Certificate clientCertificate) throws CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        caCertificate.checkValidity();
        clientCertificate.checkValidity();
        clientCertificate.verify(caCertificate.getPublicKey());
    }

    public static KeyPairData createSelfSignedKeyPairData(String issuerAndSubject, Long duration, TimeUnit timeUnit) throws PKIException {
        try {
            KeyPair keyPair = generateKeyPair();
            X509Certificate x509Certificate = createSelfSignedCertificate(issuerAndSubject, duration, timeUnit, keyPair);
            return new KeyPairData(keyPair.getPrivate(), x509Certificate);
        } catch (Exception e) {
            throw new PKIException(e);
        }
    }

    public static KeyPairData createSignedKeyPairData(String issuer, String subject, Long duration, TimeUnit timeUnit, PrivateKey privateKey) throws PKIException {
        try {
            KeyPair keyPair = generateKeyPair();
            X509Certificate x509Certificate = createSignedCertificate(issuer, subject, duration, timeUnit, keyPair.getPublic(), privateKey);
            return new KeyPairData(keyPair.getPrivate(), x509Certificate);
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

}
