package itx.iamservice.core.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.RSAKey;
import itx.iamservice.core.model.KeyId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.KeyPairSerialized;
import itx.iamservice.core.model.PKIException;
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NimbusJoseTests {


    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testRSAKeyParse() throws Exception {
        String keyId = UUID.randomUUID().toString();
        KeyPair keyPair = TokenUtils.generateKeyPair();
        X509Certificate x509Certificate = TokenUtils.createSelfSignedCertificate("issuer", 24L, TimeUnit.HOURS, keyPair);
        KeyPairData keyPairData = new KeyPairData(KeyId.from(keyId), keyPair.getPrivate(), x509Certificate);
        KeyPairSerialized keyPairSerialized = ModelUtils.serializeKeyPair(keyPairData);

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        byte[] modulusBase64 = Base64.getEncoder().encode(publicKey.getModulus().toString().getBytes(StandardCharsets.UTF_8));
        byte[] exponentBase64 = Base64.getEncoder().encode(publicKey.getPublicExponent().toString().getBytes(StandardCharsets.UTF_8));
        String modulusBase64String = new String(modulusBase64, StandardCharsets.UTF_8);
        String exponentBase64String = new String(exponentBase64, StandardCharsets.UTF_8);

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

    }

}
