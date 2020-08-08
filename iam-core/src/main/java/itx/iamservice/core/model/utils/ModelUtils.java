package itx.iamservice.core.model.utils;

import itx.iamservice.core.IAMModelBuilders;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.KeyPairSerialized;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.RoleImpl;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.builders.ClientBuilder;
import itx.iamservice.core.model.builders.ModelBuilder;
import itx.iamservice.core.model.builders.OrganizationBuilder;
import itx.iamservice.core.model.builders.ProjectBuilder;
import itx.iamservice.core.model.builders.UserBuilder;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.impl.caches.ModelCacheImpl;
import itx.iamservice.core.services.impl.persistence.LoggingPersistenceServiceImpl;
import itx.iamservice.core.services.persistence.PersistenceService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ModelUtils.class);

    public static final String IAM_ADMINS_NAME = "iam-admins";
    public static final OrganizationId IAM_ADMINS_ORG = OrganizationId.from(IAM_ADMINS_NAME);
    public static final ProjectId IAM_ADMINS_PROJECT = ProjectId.from(IAM_ADMINS_NAME);
    public static final UserId IAM_ADMIN_USER = UserId.from("admin");
    public static final ClientId IAM_ADMIN_CLIENT_ID = ClientId.from("admin-client");
    private static final Collection<String> IAM_AUDIENCE = Arrays.asList(IAM_ADMINS_NAME);

    public static final String IAM_SERVICE = "iam-admin-service";
    public static final String ACTION_ALL = "all";
    public static final String ACTION_READ = "read";
    public static final String ACTION_MODIFY = "modify";
    public static final String ORGANIZATIONS_RESOURCE = "organizations";
    public static final String ORGANIZATION_RESOURCE = "organization";
    public static final String PROJECTS_RESOURCE = "projects";
    public static final String PROJECT_RESOURCE = "project";
    public static final String USERS_RESOURCE = "users";
    public static final String USER_RESOURCE = "user";
    public static final String CLIENTS_RESOURCE = "clients";
    public static final String CLIENT_RESOURCE = "client";

    private ModelUtils() {
    }

    public static ModelCache createEmptyModelCache(PersistenceService persistenceService, ModelId id, String modelName) {
        Model model = new ModelImpl(id, modelName);
        return new ModelCacheImpl(model, persistenceService);
    }
    public static ModelCache createDefaultModelCache(String iamAdminPassword, String iamClientSecret) throws PKIException {
        return createDefaultModelCache(iamAdminPassword, iamClientSecret, new LoggingPersistenceServiceImpl());
    }

    public static ModelCache createDefaultModelCache(String iamAdminPassword, String iamClientSecret, PersistenceService persistenceService) throws PKIException {
        return createDefaultModelCache(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, iamAdminPassword, iamClientSecret, persistenceService);
    }

    public static ModelCache createDefaultModelCache(OrganizationId organizationId, ProjectId projectId, String iamAdminPassword, String iamClientSecret, PersistenceService persistenceService) throws PKIException {

        Role iamAdmin = IAMModelBuilders.roleBuilder(RoleId.from("iam-admin"), "Manage IAM-Service")
                .addPermission(IAM_SERVICE, ORGANIZATIONS_RESOURCE, ACTION_ALL)
                .addPermission(IAM_SERVICE, PROJECTS_RESOURCE, ACTION_ALL)
                .addPermission(IAM_SERVICE, USERS_RESOURCE, ACTION_ALL)
                .addPermission(IAM_SERVICE, CLIENTS_RESOURCE, ACTION_ALL)
                .build();

        Role iamClientRole = IAMModelBuilders.roleBuilder(RoleId.from("iam-admin-client"), "IAM Client role.")
                .addPermission(IAM_SERVICE, ORGANIZATION_RESOURCE, ACTION_READ)
                .build();

        ModelId id = ModelId.from("default-model-001");
        String modelName = "Default Model";

        LOG.info("#MODEL: Initializing default model id={} name={} ...", id, modelName);
        LOG.info("#MODEL: Default organizationId={}, projectId={}", IAM_ADMINS_ORG.getId(), IAM_ADMINS_PROJECT.getId());
        LOG.info("#MODEL:    Default admin userId={}", IAM_ADMIN_USER.getId());
        LOG.info("#MODEL:    Default client credentials clientId={} clientSecret={}", IAM_ADMIN_CLIENT_ID.getId(), iamClientSecret);
        return IAMModelBuilders.modelBuilder(id, modelName, persistenceService)
                .addOrganization(organizationId, IAM_ADMINS_NAME)
                .addProject(projectId, IAM_ADMINS_NAME, IAM_AUDIENCE)
                    .addRole(iamAdmin)
                    .addRole(iamClientRole)
                    .addClient(IAM_ADMIN_CLIENT_ID, "client-1", iamClientSecret)
                        .addRole(iamClientRole.getId())
                    .and()
                    .addUser(IAM_ADMIN_USER, "iam-admin")
                        .addUserNamePasswordCredentials(IAM_ADMIN_USER, iamAdminPassword)
                        .addRole(iamAdmin.getId())
                .build();
    }

    public static OrganizationInfo createOrganizationInfo(Organization organization) throws CertificateEncodingException {
        Set<ProjectId> projects = organization.getProjects().stream().collect(Collectors.toSet());
        return new OrganizationInfo(organization.getId(), organization.getName(), projects, organization.getKeyPairData());
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
     * Parse set of scopes from {@ling String} containing space delimited, case sensitive scopes.
     * @param scope {@ling String} containing scopes.
     * @return parsed {@link Set} of scopes.
     */
    public static Set<RoleId> getScopes(String scope) {
        if (scope == null) {
            return Collections.emptySet();
        } else {
            Set<RoleId> scopes = new HashSet<>();
            String[] rawScopes = scope.trim().split(" ");
            for (String s: rawScopes) {
                if (!s.isEmpty()) {
                    scopes.add(RoleId.from(s));
                }
            }
            return scopes;
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
     * @param persistenceService - persistence service
     * @return
     * @throws PKIException
     */
    public static ModelCache createModel(int organizations, int projects, int clients, int users, int permissions, int roles, PersistenceService persistenceService) throws PKIException {
        ModelBuilder modelBuilder = IAMModelBuilders.modelBuilder(ModelId.from("test-model"), "test-model", persistenceService);
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
                    UserBuilder builder = projectBuilder.addUser(userId, "name");
                    projectRoles.forEach(r->builder.addRole(r.getId()));
                    builder.addUserNamePasswordCredentials(userId, "secret");
                }
            }
        }
        return modelBuilder.build();
    }

}
