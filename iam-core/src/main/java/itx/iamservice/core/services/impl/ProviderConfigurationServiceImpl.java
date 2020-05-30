package itx.iamservice.core.services.impl;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.User;
import itx.iamservice.core.services.ProviderConfigurationService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.dto.JWKData;
import itx.iamservice.core.services.dto.JWKResponse;
import itx.iamservice.core.services.dto.ProviderConfigurationRequest;
import itx.iamservice.core.services.dto.ProviderConfigurationResponse;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public class ProviderConfigurationServiceImpl implements ProviderConfigurationService {

    private static final String[] responseTypes = { "code", "code id_token","code token","code id_token token" };
    private static final String[] grantTypes = { "authorization_code", "refresh_token", "password", "client_credentials" };
    private static final String[] subjectTypesSupported = { "public","pairwise" };
    private static final String[] idTokenSigningAlgValuesSupported = {"PS384","ES384","RS384","HS256","HS512","ES256","RS256","HS384","ES512","PS256","PS512","RS512"};
    private static final String[] idTokenEncryptionAlgValuesSupported = { "RSA" };

    public static final String KEY_TYPE = "RSA";
    public static final String KEY_USE = "sig";
    public static final String KEY_ALGORITHM = "RS256";

    public static String[] getOperations() {
        return new String[] { "verify" };
    }

    private final ProjectManagerService projectManagerService;

    public ProviderConfigurationServiceImpl(ProjectManagerService projectManagerService) {
        this.projectManagerService = projectManagerService;
    }

    @Override
    public ProviderConfigurationResponse getConfiguration(ProviderConfigurationRequest request) {
        Collection<Role> roles = projectManagerService.getRoles(request.getOrganizationId(), request.getProjectId());
        String[] scopesSupported = roles.stream().map(r->r.getId().getId()).toArray(n-> new String[n]);
        String issuer = request.getBaseURL() + "/" + request.getOrganizationId().getId() + "/" + request.getProjectId();
        String authorizationEndpoint = issuer + "/authorize";
        String tokenEndpoint = issuer + "/token";
        String jwksUri = issuer + "/.well-known/jwks.json";
        String introspectionEndpoint = issuer + "/introspect";
        String revocationEndpoint = issuer + "/revoke";
        return new ProviderConfigurationResponse(issuer, authorizationEndpoint, tokenEndpoint, jwksUri,
                scopesSupported, responseTypes, grantTypes, subjectTypesSupported,
                idTokenSigningAlgValuesSupported, idTokenEncryptionAlgValuesSupported,
                introspectionEndpoint, revocationEndpoint);
    }

    @Override
    public JWKResponse getJWKData(OrganizationId organizationId, ProjectId projectId) {
        Collection<User> users = projectManagerService.getUsers(organizationId, projectId);
        List<JWKData> keys = new ArrayList<>();
        users.forEach(u -> {
            RSAPublicKey publicKey = (RSAPublicKey) u.getKeyPairData().getPublicKey();
            byte[] modulusBase64 = Base64.getEncoder().encode(publicKey.getModulus().toString().getBytes(StandardCharsets.UTF_8));
            byte[] exponentBase64 = Base64.getEncoder().encode(publicKey.getPublicExponent().toString().getBytes(StandardCharsets.UTF_8));
            String modulusBase64String = new String(modulusBase64, StandardCharsets.UTF_8);
            String exponentBase64String = new String(exponentBase64, StandardCharsets.UTF_8);
            JWKData jwkData = new JWKData(u.getKeyPairData().getId().getId(), KEY_TYPE, KEY_USE, KEY_ALGORITHM, getOperations(),
                    u.getKeyPairSerialized().getX509Certificate(), modulusBase64String, exponentBase64String);
            keys.add(jwkData);
        });
        return new JWKResponse(keys);
    }

}
