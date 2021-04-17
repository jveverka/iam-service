package one.microproject.iamservice.core.services.impl;

import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.utils.TokenUtils;
import one.microproject.iamservice.core.services.ProviderConfigurationService;
import one.microproject.iamservice.core.services.admin.OrganizationManagerService;
import one.microproject.iamservice.core.services.admin.ProjectManagerService;
import one.microproject.iamservice.core.dto.JWKData;
import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.services.dto.ProviderConfigurationRequest;
import one.microproject.iamservice.core.dto.ProviderConfigurationResponse;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ProviderConfigurationServiceImpl implements ProviderConfigurationService {

    public static final String KEY_TYPE = "RSA";
    public static final String KEY_USE = "sig";
    public static final String KEY_ALGORITHM = "RS256";

    private static final String[] responseTypes = { "code", "code id_token","code token","code id_token token" };
    private static final String[] grantTypes = { "authorization_code", "refresh_token", "password", "client_credentials" };
    private static final String[] subjectTypesSupported = { "public", "pairwise" };
    private static final String[] idTokenSigningAlgValuesSupported = { KEY_ALGORITHM };
    private static final String[] idTokenEncryptionAlgValuesSupported = { KEY_TYPE };

    public static String[] getOperations() {
        return new String[] { "verify" };
    }

    private final OrganizationManagerService organizationManagerService;
    private final ProjectManagerService projectManagerService;

    public ProviderConfigurationServiceImpl(OrganizationManagerService organizationManagerService,
                                            ProjectManagerService projectManagerService) {
        this.organizationManagerService = organizationManagerService;
        this.projectManagerService = projectManagerService;
    }

    @Override
    public ProviderConfigurationResponse getConfiguration(ProviderConfigurationRequest request) {
        Collection<Permission> permissions = projectManagerService.getPermissions(request.getOrganizationId(), request.getProjectId());
        String[] scopesSupported = permissions.stream().map(p->p.getId().getId()).toArray(n-> new String[n]);
        String baseUrl = request.getBaseURL() + "/" + request.getOrganizationId().getId() + "/" + request.getProjectId();
        String issuer = baseUrl;
        String authorizationEndpoint = baseUrl + "/authorize";
        String tokenEndpoint = baseUrl + "/token";
        String jwksUri = baseUrl + "/.well-known/jwks.json";
        String introspectionEndpoint = baseUrl + "/introspect";
        String revocationEndpoint = baseUrl + "/revoke";
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
            String modulusBase64String = Base64.getEncoder().encodeToString(TokenUtils.toBytesUnsigned(publicKey.getModulus()));
            String exponentBase64String = Base64.getEncoder().encodeToString(TokenUtils.toBytesUnsigned(publicKey.getPublicExponent()));
            JWKData jwkData = new JWKData(u.getKeyPairData().getId().getId(), KEY_TYPE, KEY_USE, KEY_ALGORITHM, getOperations(),
                    u.getKeyPairSerialized().getX509Certificate(), modulusBase64String, exponentBase64String);
            keys.add(jwkData);
        });
        return new JWKResponse(keys);
    }

    @Override
    public Optional<PublicKey> getKeyById(OrganizationId organizationId, ProjectId projectId, String kid) {
        Collection<User> users = projectManagerService.getUsers(organizationId, projectId);
        return filterKeys(users, kid);
    }

    @Override
    public Optional<PublicKey> getKeyById(OrganizationId organizationId, String kid) {
        Collection<User> users = organizationManagerService.getAllUsers(organizationId);
        return filterKeys(users, kid);
    }

    @Override
    public Optional<PublicKey> getKeyById(String kid) {
        for (Organization organization: organizationManagerService.getAll()) {
            Optional<PublicKey> key = getKeyById(organization.getId(), kid);
            if (key.isPresent()) {
                return key;
            }
        }
        return Optional.empty();
    }

    private Optional<PublicKey> filterKeys(Collection<User> users, String kid) {
        Optional<User> optionalUser = users.stream().filter(u -> u.getKeyPairData().getId().getId().equals(kid)).findFirst();
        if (optionalUser.isPresent()) {
            PublicKey publicKey = optionalUser.get().getKeyPairData().getPublicKey();
            return Optional.of(publicKey);
        }
        return Optional.empty();
    }

}
