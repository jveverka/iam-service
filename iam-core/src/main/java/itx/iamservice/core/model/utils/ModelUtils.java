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
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ModelUtils.class);

    public static final String IAM_ADMINS_NAME = "iam-admins";
    public static final OrganizationId IAM_ADMINS_ORG = OrganizationId.from(IAM_ADMINS_NAME);
    public static final ProjectId IAM_ADMINS_PROJECT = ProjectId.from(IAM_ADMINS_NAME);
    public static final UserId IAM_ADMIN_USER = UserId.from("admin");
    public static final ClientId IAM_ADMIN_CLIENT_ID = ClientId.from("admin-client");
    public static final String IAM_ADMIN_CLIENT_SECRET = "top-secret";

    public static final String IAM_SERVICE = "iam-admin-service";
    public static final String READ_ACTION = "read";
    public static final String MODIFY_ACTION = "modify";
    public static final String CREATE_ACTION = "create";
    public static final String ORGANIZATION_RESOURCE = "organizations";
    public static final String PROJECTS_RESOURCE = "projects";
    public static final String USERS_RESOURCE = "users";
    public static final String CLIENTS_RESOURCE = "clients";
    public static final String ROLES_RESOURCE = "roles";
    public static final String PERMISSIONS_RESOURCE = "permissions";

    private ModelUtils() {
    }

    public static ModelCache createEmptyModelCache(PersistenceService persistenceService, ModelId id, String modelName) {
        Model model = new ModelImpl(id, modelName);
        return new ModelCacheImpl(model, persistenceService);
    }
    public static ModelCache createDefaultModelCache(String iamAdminPassword) throws PKIException {
        return createDefaultModelCache(iamAdminPassword, new LoggingPersistenceServiceImpl());
    }

    public static ModelCache createDefaultModelCache(String iamAdminPassword, PersistenceService persistenceService) throws PKIException {

        Role manageOrganizationsRole = IAMModelBuilders.roleBuilder(RoleId.from("manage-organizations"), "Can manage organizations.")
                .addPermission(IAM_SERVICE, ORGANIZATION_RESOURCE, READ_ACTION)
                .addPermission(IAM_SERVICE, ORGANIZATION_RESOURCE, MODIFY_ACTION)
                .addPermission(IAM_SERVICE, ORGANIZATION_RESOURCE, CREATE_ACTION)
                .build();

        Role manageProjectsRole = IAMModelBuilders.roleBuilder(RoleId.from("manage-projects"), "Can manage projects.")
                .addPermission(IAM_SERVICE, PROJECTS_RESOURCE, READ_ACTION)
                .addPermission(IAM_SERVICE, PROJECTS_RESOURCE, MODIFY_ACTION)
                .addPermission(IAM_SERVICE, PROJECTS_RESOURCE, CREATE_ACTION)
                .build();

        Role manageUsersRole = IAMModelBuilders.roleBuilder(RoleId.from("manage-users"), "Can manage users.")
                .addPermission(IAM_SERVICE, USERS_RESOURCE, READ_ACTION)
                .addPermission(IAM_SERVICE, USERS_RESOURCE, MODIFY_ACTION)
                .addPermission(IAM_SERVICE, USERS_RESOURCE, CREATE_ACTION)
                .build();

        Role manageClientsRole = IAMModelBuilders.roleBuilder(RoleId.from("manage-clients"), "Can manage clients.")
                .addPermission(IAM_SERVICE, CLIENTS_RESOURCE, READ_ACTION)
                .addPermission(IAM_SERVICE, CLIENTS_RESOURCE, MODIFY_ACTION)
                .addPermission(IAM_SERVICE, CLIENTS_RESOURCE, CREATE_ACTION)
                .build();

        Role manageRolesRole = IAMModelBuilders.roleBuilder(RoleId.from("manage-roles"), "Can manage roles.")
                .addPermission(IAM_SERVICE, ROLES_RESOURCE, READ_ACTION)
                .addPermission(IAM_SERVICE, ROLES_RESOURCE, MODIFY_ACTION)
                .addPermission(IAM_SERVICE, ROLES_RESOURCE, CREATE_ACTION)
                .build();

        Role managePermissionsRole = IAMModelBuilders.roleBuilder(RoleId.from("manage-permissions"), "Can manage permissions.")
                .addPermission(IAM_SERVICE, PERMISSIONS_RESOURCE, READ_ACTION)
                .addPermission(IAM_SERVICE, PERMISSIONS_RESOURCE, MODIFY_ACTION)
                .addPermission(IAM_SERVICE, PERMISSIONS_RESOURCE, CREATE_ACTION)
                .build();

        Role clientReaderRole = IAMModelBuilders.roleBuilder(RoleId.from("read-organizations"), "Can read organizations.")
                .addPermission(IAM_SERVICE, ORGANIZATION_RESOURCE, READ_ACTION)
                .build();

        ModelId id = ModelId.from("default-model-001");
        String modelName = "Default Model";

        LOG.info("#MODEL: Initializing default model id={} name={} ...", id, modelName);
        LOG.info("#MODEL: Default organizationId={}, projectId={}", IAM_ADMINS_ORG.getId(), IAM_ADMINS_PROJECT.getId());
        LOG.info("#MODEL:    Default admin userId={}", IAM_ADMIN_USER.getId());
        LOG.info("#MODEL:    Default client credentials clientId={} clientSecret={}", IAM_ADMIN_CLIENT_ID.getId(), IAM_ADMIN_CLIENT_SECRET);
        return IAMModelBuilders.modelBuilder(id, modelName, persistenceService)
                .addOrganization(IAM_ADMINS_ORG, IAM_ADMINS_NAME)
                .addProject(IAM_ADMINS_PROJECT, IAM_ADMINS_NAME)
                    .addRole(manageOrganizationsRole)
                    .addRole(manageProjectsRole)
                    .addRole(manageUsersRole)
                    .addRole(manageClientsRole)
                    .addRole(manageRolesRole)
                    .addRole(managePermissionsRole)
                    .addRole(clientReaderRole)
                    .addClient(IAM_ADMIN_CLIENT_ID, "client-1", IAM_ADMIN_CLIENT_SECRET)
                        .addRole(clientReaderRole.getId())
                    .and()
                    .addUser(IAM_ADMIN_USER, "iam-admin")
                        .addUserNamePasswordCredentials(IAM_ADMIN_USER, iamAdminPassword)
                        .addRole(manageOrganizationsRole.getId())
                        .addRole(manageProjectsRole.getId())
                        .addRole(manageUsersRole.getId())
                        .addRole(manageClientsRole.getId())
                        .addRole(manageRolesRole.getId())
                        .addRole(managePermissionsRole.getId())
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

}
