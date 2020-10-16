package itx.iamservice.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import itx.iamservice.client.IAMClient;
import itx.iamservice.core.dto.JWKData;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static itx.iamservice.client.JWTUtils.convert;
import static itx.iamservice.client.JWTUtils.validateToken;
import static itx.iamservice.core.ModelCommons.getServiceId;

public class IAMClientImpl implements IAMClient {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientImpl.class);

    private final URL baseUrl;
    private final IAMServiceProxy iamServiceProxy;
    private final ProjectId projectId;
    private final ObjectMapper mapper;
    private final URI issuer;

    public IAMClientImpl(URL baseUrl, IAMServiceProxy iamServiceProxy, OrganizationId organizationId, ProjectId projectId) throws URISyntaxException {
        this.baseUrl = baseUrl;
        this.iamServiceProxy = iamServiceProxy;
        this.projectId = projectId;
        this.mapper = new ObjectMapper();
        this.issuer = new URI(baseUrl.toString() + "/" + getServiceId(organizationId, projectId));
    }

    @Override
    public boolean waitForInit(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return iamServiceProxy.waitForInit(timeout, timeUnit);
    }

    @Override
    public Optional<JWTClaimsSet> validate(JWToken token) {
        return validate(issuer.toString(), token);
    }

    @Override
    public Optional<JWTClaimsSet> validate(OrganizationId organizationId, ProjectId projectId, JWToken token) {
        String issuerValue = baseUrl.toString() + "/" + getServiceId(organizationId, projectId);
        return validate(issuerValue, token);
    }

    @Override
    public boolean validate(OrganizationId organizationId, ProjectId projectId,
                            Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions,
                            JWToken token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token.getToken());
            String keyId = signedJWT.getHeader().getKeyID();
            Optional<JWKData> first = iamServiceProxy.getJWKResponse().getKeys().stream().filter(jwkData -> keyId.equals(jwkData.getKeyId())).findFirst();
            if (first.isPresent()) {
                JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
                String jsonString = mapper.writeValueAsString(first.get());
                JSONObject jsonObject = (JSONObject)parser.parse(jsonString);
                RSAKey rsaKey = RSAKey.parse(jsonObject);
                return validateToken(convert(rsaKey), issuer, projectId, requiredAdminPermissions, requiredApplicationPermissions, token);
            }
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        return false;
    }

    @Override
    public boolean validate(OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredApplicationPermissions, JWToken token) {
        return validate(organizationId, projectId, Set.of(), requiredApplicationPermissions, token);
    }

    @Override
    public void close() throws Exception {
        iamServiceProxy.close();
    }

    private Optional<JWTClaimsSet> validate(String issuerValue, JWToken token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token.getToken());
            String keyId = signedJWT.getHeader().getKeyID();
            Optional<JWKData> first = iamServiceProxy.getJWKResponse().getKeys().stream().filter(jwkData -> keyId.equals(jwkData.getKeyId())).findFirst();
            if (first.isPresent()) {
                JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
                String jsonString = mapper.writeValueAsString(first.get());
                JSONObject jsonObject = (JSONObject)parser.parse(jsonString);
                RSAKey rsaKey = RSAKey.parse(jsonObject);
                return validateToken(convert(rsaKey), projectId, issuerValue, token);
            }
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        LOG.debug("token validation has failed.");
        return Optional.empty();
    }

}
