package itx.iamservice.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import itx.iamservice.client.IAMClient;
import itx.iamservice.core.dto.JWKData;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class IAMClientImpl implements IAMClient {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientImpl.class);

    private final IAMServiceProxy iamServiceProxy;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final ObjectMapper mapper;

    public IAMClientImpl(IAMServiceProxy iamServiceProxy, OrganizationId organizationId, ProjectId projectId) {
        this.iamServiceProxy = iamServiceProxy;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.mapper = new ObjectMapper();
    }

    @Override
    public Optional<JWTClaimsSet> validate(JWToken jwToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwToken.getToken());
            String keyId = signedJWT.getHeader().getKeyID();
            Optional<JWKData> first = iamServiceProxy.getJWKResponse().getKeys().stream().filter(jwkData -> keyId.equals(jwkData.getKeyId())).findFirst();
            if (first.isPresent()) {
                JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
                String jsonString = mapper.writeValueAsString(first.get());
                JSONObject jsonObject = (JSONObject)parser.parse(jsonString);
                RSAKey rsaKey = RSAKey.parse(jsonObject);
                RSASSAVerifier rsassaVerifier = new RSASSAVerifier(rsaKey);
                if (signedJWT.verify(rsassaVerifier)) {
                    return Optional.of(signedJWT.getJWTClaimsSet());
                }
            }
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        return Optional.empty();
    }

    @Override
    public void close() throws Exception {
        iamServiceProxy.close();
    }

}
