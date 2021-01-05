package one.microproject.iamservice.core.utils;

import one.microproject.iamservice.core.IAMModelBuilders;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.KeyPairData;
import one.microproject.iamservice.core.model.KeyPairSerialized;
import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.ModelImpl;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.RoleImpl;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.model.builders.ClientBuilder;
import one.microproject.iamservice.core.model.builders.ModelBuilder;
import one.microproject.iamservice.core.model.builders.OrganizationBuilder;
import one.microproject.iamservice.core.model.builders.ProjectBuilder;
import one.microproject.iamservice.core.model.builders.UserBuilder;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.OrganizationInfo;
import one.microproject.iamservice.core.services.dto.Scope;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.impl.persistence.LoggingPersistenceServiceImpl;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.ModelCommons;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapperImpl;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ModelUtils.class);

    public static final String MODEL_VERSION = "2.4.1-RELEASE";
    public static final Model DEFAULT_MODEL = new ModelImpl(ModelId.from("default-model-001"), "Default Model");
    public static final String IAM_ADMINS_NAME = "iam-admins";
    public static final OrganizationId IAM_ADMINS_ORG = OrganizationId.from(IAM_ADMINS_NAME);
    public static final ProjectId IAM_ADMINS_PROJECT = ProjectId.from(IAM_ADMINS_NAME);
    public static final UserId IAM_ADMIN_USER = UserId.from("admin");
    public static final ClientId IAM_ADMIN_CLIENT_ID = ClientId.from("admin-client");
    public static final Collection<String> IAM_AUDIENCE = Collections.unmodifiableCollection(Arrays.asList(IAM_ADMINS_NAME));
    public static final long DURATION_10YEARS = 365*10L;

    private ModelUtils() {
    }

    public static ModelCache createEmptyModelCache(PersistenceService persistenceService, ModelId id, String modelName) {
        Model model = new ModelImpl(id, modelName);
        ModelWrapper modelWrapper =  new ModelWrapperImpl(model, persistenceService, false);
        return new ModelCacheImpl(modelWrapper);
    }

    public static ModelCache createDefaultModelCache(String iamAdminPassword, String iamClientSecret, String iamAdminEmail, Boolean enableClientCredentialsFlow) throws PKIException {
        ModelWrapper modelWrapper =  new ModelWrapperImpl(DEFAULT_MODEL, new LoggingPersistenceServiceImpl(), false);
        return createDefaultModelCache(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, iamAdminPassword, iamClientSecret, iamAdminEmail, modelWrapper, enableClientCredentialsFlow);
    }

    public static ModelCache createDefaultModelCache(String iamAdminPassword, String iamClientSecret, String iamAdminEmail, ModelWrapper modelWrapper, Boolean enableClientCredentialsFlow) throws PKIException {
        return createDefaultModelCache(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, iamAdminPassword, iamClientSecret, iamAdminEmail, modelWrapper, enableClientCredentialsFlow);
    }

    public static ModelCache createDefaultModelCache(OrganizationId organizationId, ProjectId projectId, String iamAdminPassword, String iamClientSecret, String iamAdminEmail, ModelWrapper modelWrapper, Boolean enableClientCredentialsFlow) throws PKIException {

        Role iamGlobalAdminRole = IAMModelBuilders.roleBuilder(RoleId.from("iam-admin-global"), "Manage IAM-Service")
                .addPermission(ModelCommons.IAM_SERVICE_ORGANIZATIONS_RESOURCE_ACTION_ALL)
                .addPermission(ModelCommons.IAM_SERVICE_PROJECTS_RESOURCE_ACTION_ALL)
                .addPermission(ModelCommons.IAM_SERVICE_USERS_RESOURCE_ACTION_ALL)
                .addPermission(ModelCommons.IAM_SERVICE_CLIENTS_RESOURCE_ACTION_ALL)
                .build();

        Role iamProjectAdminRole = new RoleImpl(RoleId.from("iam-admin-project"), "",
                ModelCommons.createProjectAdminPermissions(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT));

        Role iamClientRole = IAMModelBuilders.roleBuilder(RoleId.from("iam-admin-client"), "IAM Client role.")
                .addPermission(ModelCommons.IAM_SERVICE_ORGANIZATION_RESOURCE_ACTION_READ)
                .build();

        ClientProperties properties = new ClientProperties(getIamAdminsRedirectURL(),
                true, true, enableClientCredentialsFlow, new HashMap<>());

        LOG.info("#MODEL: Initializing default model id={} name={} ...", modelWrapper.getModel().getId(), modelWrapper.getModel().getName());
        LOG.info("#MODEL: Default organizationId={}, projectId={}", IAM_ADMINS_ORG.getId(), IAM_ADMINS_PROJECT.getId());
        LOG.info("#MODEL:    Default admin userId={}", IAM_ADMIN_USER.getId());
        LOG.info("#MODEL:    Default client credentials clientId={} clientSecret={}", IAM_ADMIN_CLIENT_ID.getId(), iamClientSecret);
        return IAMModelBuilders.modelBuilder(modelWrapper)
                .addOrganization(organizationId, IAM_ADMINS_NAME)
                .addProject(projectId, IAM_ADMINS_NAME, IAM_AUDIENCE)
                    .addRole(iamGlobalAdminRole)
                    .addRole(iamClientRole)
                    .addRole(iamProjectAdminRole)
                    .addClient(IAM_ADMIN_CLIENT_ID, "Admin Client 01", iamClientSecret, properties)
                        .addRole(iamClientRole.getId())
                    .and()
                    .addUser(IAM_ADMIN_USER, "IAM Admin Superuser", iamAdminEmail, UserProperties.getDefault())
                        .addUserNamePasswordCredentials(IAM_ADMIN_USER, iamAdminPassword)
                        .addRole(iamGlobalAdminRole.getId())
                        .addRole(iamProjectAdminRole.getId())
                .build();
    }

    public static String getIamAdminsRedirectURL() {
        return getRedirectURL(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT);
    }

    public static String getRedirectURL(OrganizationId organizationId, ProjectId projectId) {
        return "http://loclahost:8080/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/redirect";
    }

    public static OrganizationInfo createOrganizationInfo(Organization organization) throws CertificateEncodingException {
        Set<String> projects = organization.getProjects().stream().map(p -> p.getId()).collect(Collectors.toSet());
        return new OrganizationInfo(organization.getId().getId(), organization.getName(), projects, organization.getKeyPairData());
    }

    public static KeyPairSerialized serializeKeyPair(KeyPairData keyPairData) throws PKIException {
        String privateKey = TokenUtils.serializePrivateKey(keyPairData.getPrivateKey());
        String certificate = TokenUtils.serializeX509Certificate(keyPairData.getX509Certificate());
        return new KeyPairSerialized(keyPairData.getId(), privateKey, certificate);
    }

    public static KeyPairData deserializeKeyPair(KeyPairSerialized keyPairData) throws PKIException {
        PrivateKey privateKey = TokenUtils.deserializePrivateKey(keyPairData.getPrivateKey());
        X509Certificate certificate = TokenUtils.deserializeX509Certificate(keyPairData.getX509Certificate());
        return new KeyPairData(keyPairData.getId(), privateKey, certificate);
    }

    public static String getSha512HashBase64(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.toBase64String(hash);
    }

    /**
     * Parse set of scopes from {@link String} containing space delimited, case sensitive scopes.
     * @param scope {@link String} containing scopes.
     * @return parsed set od {@link Scope}(s).
     */
    public static Scope getScopes(String scope) {
        if (scope == null) {
            return new Scope(Collections.emptySet());
        } else {
            Set<String> scopes = new HashSet<>();
            String[] rawScopes = scope.trim().split(" ");
            for (String s: rawScopes) {
                if (!s.isEmpty()) {
                    scopes.add(s);
                }
            }
            return new Scope(scopes);
        }
    }

    /**
     * Create Large data model for performance testing.
     * @param organizations - number of organizations in the model
     * @param projects - number of projects in organization
     * @param clients - number of clients in project 
     * @param users - number of users in project
     * @param permissions - number of permissions in project
     * @param roles - number of roles in project
     * @param modelWrapper - wrapped model data
     * @return populated instance of {@link ModelCache}
     * @throws PKIException in case of key or certificate generation error.
     */
    public static ModelCache createModel(int organizations, int projects, int clients, int users, int permissions, int roles, ModelWrapper modelWrapper) throws PKIException {
        ModelBuilder modelBuilder = IAMModelBuilders.modelBuilder(modelWrapper);
        String organizationPrefix = "organization-";
        String projectPrefix = "project-";
        String permissionPrefix = "resource-";
        String rolePrefix = "role-";
        String clientPrefix = "client-";
        String userPrefix = "user-";

        for (int orgIndex=0; orgIndex<organizations; orgIndex++) {
            OrganizationId organizationId = OrganizationId.from(organizationPrefix + orgIndex);
            OrganizationBuilder organizationBuilder = modelBuilder.addOrganization(organizationId, organizationPrefix + "name-" + orgIndex);
            for (int projectIndex=0; projectIndex<projects; projectIndex++) {
                ProjectId projectId = ProjectId.from(projectPrefix + projectIndex);
                Collection<String> audience = Set.of("service1", "service2", "service3");
                ProjectBuilder projectBuilder = organizationBuilder.addProject(projectId, projectPrefix + "name-" + projectIndex, audience);
                Set<Permission> projectPermissions = new HashSet<>();
                List<Role> projectRoles = new ArrayList<>();
                for (int permissionIndex=0; permissionIndex<permissions; permissionIndex++) {
                    Permission permission = new Permission("service1", permissionPrefix + permissionIndex,  "action");
                    projectPermissions.add(permission);
                    projectBuilder.addPermission(permission);
                }
                for (int roleIndex=0; roleIndex<roles; roleIndex++) {
                    Role role = new RoleImpl(RoleId.from(rolePrefix + roleIndex), "name", projectPermissions);
                    projectRoles.add(role);
                    projectBuilder.addRole(role);
                }
                for (int clientIndex=0; clientIndex<clients; clientIndex++) {
                    ClientBuilder builder = projectBuilder.addClient(ClientId.from(clientPrefix + clientIndex), "name",  "secret");
                    projectRoles.forEach(r->builder.addRole(r.getId()));
                }
                for (int userIndex=0; userIndex<users; userIndex++) {
                    UserId userId = UserId.from(userPrefix + userIndex);
                    UserBuilder builder = projectBuilder.addUser(userId, "name", "user@email.com", UserProperties.getDefault());
                    projectRoles.forEach(r->builder.addRole(r.getId()));
                    builder.addUserNamePasswordCredentials(userId, "secret");
                }
            }
        }
        return modelBuilder.build();
    }

    public static ModelWrapper createModelWrapper(String modelId, PersistenceService persistenceService, boolean flushOnChange) {
        return new ModelWrapperImpl(new ModelImpl(ModelId.from(modelId), ""), persistenceService, flushOnChange);
    }

    public static ModelWrapper createInMemoryModelWrapper(String modelId) {
        return new ModelWrapperImpl(new ModelImpl(ModelId.from(modelId), ""), new LoggingPersistenceServiceImpl(), false);
    }

    public static ModelWrapper createInMemoryModelWrapper(String modelId, String modelName) {
        return new ModelWrapperImpl(new ModelImpl(ModelId.from(modelId), modelName), new LoggingPersistenceServiceImpl(), false);
    }

}
