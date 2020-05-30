package itx.iamservice.core.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.KeyId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.KeyPairSerialized;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.utils.TokenUtils;
import itx.iamservice.core.services.dto.JWKData;
import itx.iamservice.core.services.impl.ProviderConfigurationServiceImpl;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NimbusJoseTests {


    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testRSAKeyParse() throws Exception {
        KeyId keyId = KeyId.from(UUID.randomUUID().toString());
        KeyPair keyPair = TokenUtils.generateKeyPair();
        X509Certificate x509Certificate = TokenUtils.createSelfSignedCertificate("issuer", 24L, TimeUnit.HOURS, keyPair);
        KeyPairData keyPairData = new KeyPairData(keyId, keyPair.getPrivate(), x509Certificate);
        KeyPairSerialized keyPairSerialized = ModelUtils.serializeKeyPair(keyPairData);

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        String modulusBase64String = Base64.getEncoder().encodeToString(TokenUtils.toBytesUnsigned(publicKey.getModulus()));
        String exponentBase64String = Base64.getEncoder().encodeToString(TokenUtils.toBytesUnsigned(publicKey.getPublicExponent()));

        JWKData jwkData = new JWKData(keyPairSerialized.getId().getId(),
                ProviderConfigurationServiceImpl.KEY_TYPE,
                ProviderConfigurationServiceImpl.KEY_USE,
                ProviderConfigurationServiceImpl.KEY_ALGORITHM,
                ProviderConfigurationServiceImpl.getOperations(), keyPairSerialized.getX509Certificate(),
                modulusBase64String, exponentBase64String);

        ObjectMapper mapper = new ObjectMapper();
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        String jsonString = mapper.writeValueAsString(jwkData);
        JSONObject jsonObject = (JSONObject)parser.parse(jsonString);

        RSAKey rsaKey = RSAKey.parse(jsonObject);
        assertNotNull(rsaKey);

        PublicKey publicKeyFromRSAKey = rsaKey.toPublicKey();
        assertNotNull(publicKeyFromRSAKey);

        Set<String> roles = new HashSet<>();
        roles.add("r1");
        roles.add("r2");

        JWToken jwToken = TokenUtils.issueToken(OrganizationId.from("org"), ProjectId.from("p1"), UserId.from("u1"), 10L,
                TimeUnit.HOURS, roles, keyId, keyPair.getPrivate(), TokenType.BEARER);

        SignedJWT signedJWT = SignedJWT.parse(jwToken.getToken());
        RSASSAVerifier rsassaVerifier = new RSASSAVerifier(rsaKey);
        boolean result = rsassaVerifier.verify(signedJWT.getHeader(), signedJWT.getSigningInput(), signedJWT.getSignature());
        assertTrue(result);
    }

    @Test
    public void testJWTVerification() throws Exception {
        KeyId keyId = KeyId.from(UUID.randomUUID().toString());
        KeyPair keyPair = TokenUtils.generateKeyPair();
        X509Certificate x509Certificate = TokenUtils.createSelfSignedCertificate("issuer", 24L, TimeUnit.HOURS, keyPair);
        KeyPairData keyPairData = new KeyPairData(keyId, keyPair.getPrivate(), x509Certificate);
        KeyPairSerialized keyPairSerialized = ModelUtils.serializeKeyPair(keyPairData);

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String modulusBase64String = Base64.getEncoder().encodeToString(TokenUtils.toBytesUnsigned(publicKey.getModulus()));
        String exponentBase64String = Base64.getEncoder().encodeToString(TokenUtils.toBytesUnsigned(publicKey.getPublicExponent()));

        JWKData jwkData = new JWKData(keyPairSerialized.getId().getId(),
                ProviderConfigurationServiceImpl.KEY_TYPE,
                ProviderConfigurationServiceImpl.KEY_USE,
                ProviderConfigurationServiceImpl.KEY_ALGORITHM,
                ProviderConfigurationServiceImpl.getOperations(), keyPairSerialized.getX509Certificate(),
                modulusBase64String, exponentBase64String);

        Set<String> roles = new HashSet<>();
        roles.add("r1");
        roles.add("r2");

        JWToken jwToken = TokenUtils.issueToken(OrganizationId.from("org"), ProjectId.from("p1"), UserId.from("u1"), 10L,
                TimeUnit.HOURS, roles, keyId, keyPair.getPrivate(), TokenType.BEARER);

        SignedJWT signedJWT = SignedJWT.parse(jwToken.getToken());
        DefaultJWTProcessor defaultJWTProcessor = new DefaultJWTProcessor();
        defaultJWTProcessor.setJWSKeySelector(new JWSKeySelectorImpl(jwkData));
        JWTClaimsSet jwtClaimsSet = defaultJWTProcessor.process(signedJWT, null);
        assertNotNull(jwtClaimsSet);
        assertNotNull("org", jwtClaimsSet.getIssuer());
    }

    private class JWSKeySelectorImpl implements JWSKeySelector {
        private final JWKData jwkData;
        private JWSKeySelectorImpl(JWKData jwkData) {
            this.jwkData = jwkData;
        }
        @Override
        public List<? extends Key> selectJWSKeys(JWSHeader header, SecurityContext context) throws KeySourceException {
            List<Key> keys = new ArrayList<>();
            try {
                X509Certificate x509Certificate = TokenUtils.deserializeX509Certificate(jwkData.getX509CertificateSHA256Thumbprint());
                keys.add(x509Certificate.getPublicKey());
            } catch (PKIException e) {
                e.printStackTrace();
            }
            return keys;
        }
    }

}
