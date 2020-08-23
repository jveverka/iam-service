package itx.iamservice.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import itx.iamservice.client.IAMClient;
import itx.iamservice.client.JWTUtils;
import itx.iamservice.core.dto.JWKData;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static itx.iamservice.core.ModelCommons.getServiceId;

public class IAMClientImpl implements IAMClient {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientImpl.class);

    private final IAMServiceProxy iamServiceProxy;
    private final ProjectId projectId;
    private final ObjectMapper mapper;
    private final String issuer;

    public IAMClientImpl(IAMServiceProxy iamServiceProxy, OrganizationId organizationId, ProjectId projectId) {
        this.iamServiceProxy = iamServiceProxy;
        this.projectId = projectId;
        this.mapper = new ObjectMapper();
        this.issuer = getServiceId(organizationId, projectId);
    }

    @Override
    public Optional<JWTClaimsSet> validate(JWToken token) {
        return validate(issuer, token);
    }

    @Override
    public Optional<JWTClaimsSet> validate(OrganizationId organizationId, ProjectId projectId, JWToken token) {
        String issuerValue = getServiceId(organizationId, projectId);
        return validate(issuerValue, token);
    }

    @Override
    public boolean validate(OrganizationId organizationId, ProjectId projectId,
                            Set<Permission> requiredAdminPermissions, Set<Permission> requiredApplicationPermissions,
                            JWToken token) {
        String issuerValue = getServiceId(organizationId, projectId);
        Optional<JWTClaimsSet> claimSet = validate(issuerValue, token);
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
            }
        }
        return true;
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
                RSASSAVerifier rsassaVerifier = new RSASSAVerifier(rsaKey);
                if (signedJWT.verify(rsassaVerifier) &&
                        issuerValue.equals(signedJWT.getJWTClaimsSet().getIssuer()) &&
                        signedJWT.getJWTClaimsSet().getAudience().contains(projectId.getId())) {
                    return Optional.of(signedJWT.getJWTClaimsSet());
                }
            }
        } catch (Exception e) {
            LOG.info("Exception: ", e);
        }
        return Optional.empty();
    }

}
