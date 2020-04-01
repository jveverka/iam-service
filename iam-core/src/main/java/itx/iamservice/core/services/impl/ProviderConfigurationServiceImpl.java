package itx.iamservice.core.services.impl;

import itx.iamservice.core.model.KeyPairSerialized;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.services.ProviderConfigurationService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.dto.JWKData;
import itx.iamservice.core.services.dto.JWKResponse;
import itx.iamservice.core.services.dto.ProviderConfigurationRequest;
import itx.iamservice.core.services.dto.ProviderConfigurationResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProviderConfigurationServiceImpl implements ProviderConfigurationService {

    private static final String[] responseTypes = { "code", "code id_token","code token","code id_token token" };
    private static final String[] grantTypes = { "authorization_code", "refresh_token", "password", "client_credentials" };
    private static final String[] subjectTypesSupported = { "public","pairwise" };
    private static final String[] idTokenSigningAlgValuesSupported = {"PS384","ES384","RS384","HS256","HS512","ES256","RS256","HS384","ES512","PS256","PS512","RS512"};
    private static final String[] idTokenEncryptionAlgValuesSupported = { "RSA" };

    private static final String KEY_TYPE = "RSA";
    private static final String KEY_USE = "sig";
    private static final String[] KEY_OPERATIONS = { "verify" };
    private static final String KEY_ALGORITHM = "SHA256withRSA";

    private final ProjectManagerService projectManagerService;

    public ProviderConfigurationServiceImpl(ProjectManagerService projectManagerService) {
        this.projectManagerService = projectManagerService;
    }

    @Override
    public ProviderConfigurationResponse getConfiguration(ProviderConfigurationRequest request) {
        Collection<Role> roles = projectManagerService.getRoles(request.getOrganizationId(), request.getProjectId());
        String[] scopesSupported = roles.stream().map(r->r.getId().getId()).toArray(n-> new String[n]);
        String issuer = request.getBaseURL() + "/" + request.getOrganizationId().getId() + "/" + request.getProjectId();
        String authorizationEndpoint = issuer + "/auth";
        String tokenEndpoint = issuer + "/token";
        String jwksUri = issuer + "/.well-known/jwks.json";
        return new ProviderConfigurationResponse(issuer, authorizationEndpoint, tokenEndpoint, null, jwksUri,
                scopesSupported, responseTypes, grantTypes, subjectTypesSupported, idTokenSigningAlgValuesSupported, idTokenEncryptionAlgValuesSupported);
    }

    @Override
    public JWKResponse getJWKData(OrganizationId organizationId, ProjectId projectId) {
        Optional<Project> projectOptional = projectManagerService.get(organizationId, projectId);
        if (projectOptional.isPresent()) {
            List<KeyPairSerialized> keyPairs = projectOptional.get().getUsers().stream().map(u -> u.getKeyPairSerialized()).collect(Collectors.toList());
            List<JWKData> keys = new ArrayList<>();
            keyPairs.forEach(k -> {
                JWKData jwkData = new JWKData(k.getId().getId(), KEY_TYPE, KEY_USE, KEY_ALGORITHM, KEY_OPERATIONS, k.getX509Certificate());
                keys.add(jwkData);
            });
            return new JWKResponse(keys);
        }
        return new JWKResponse();
    }

}
