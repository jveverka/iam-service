package itx.iamservice.core.model.utils;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.KeyPairSerialized;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.OrganizationImpl;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.ProjectImpl;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.UserImpl;
import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;
import itx.iamservice.core.services.dto.OrganizationInfo;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ModelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ModelUtils.class);

    private static final String IAM_ADMINS_NAME = "iam-admins";
    public static final OrganizationId IAM_ADMINS_ORG = OrganizationId.from(IAM_ADMINS_NAME);
    public static final ProjectId IAM_ADMINS_PROJECT = ProjectId.from(IAM_ADMINS_NAME);
    public static final UserId IAM_ADMIN_USER = UserId.from("admin");
    public static final ClientId IAM_ADMIN_CLIENT_ID = ClientId.from("admin-client");
    public static final ClientCredentials IAM_ADMIN_CLIENT_CREDENTIALS = new ClientCredentials(IAM_ADMIN_CLIENT_ID, "top-secret");

    public static final String IAM_SERVICE = "iam-admin-service";
    public static final String READ_ACTION = "read";
    public static final String MODIFY_ACTION = "modify";

    private ModelUtils() {
    }

    public static String createId() {
        return UUID.randomUUID().toString();
    }

    public static OrganizationId createOrganizationId() {
        return OrganizationId.from(createId());
    }

    public static ProjectId createProjectId() {
        return ProjectId.from(createId());
    }

    public static UserId createUserId() {
        return UserId.from(createId());
    }

    public static RoleId createRoleId() {
        return RoleId.from(createId());
    }

    public static Model createDefaultModel(String iamAdminPassword) throws PKIException {
        String name = "default-model";
        ModelId id = ModelId.from(UUID.randomUUID().toString());
        LOG.info("#MODEL: Initializing default model id={} name={} ...", id, name);
        LOG.info("#MODEL: Default organizationId={}, projectId={}", IAM_ADMINS_ORG.getId(), IAM_ADMINS_PROJECT.getId());
        LOG.info("#MODEL:    Default admin userId={}", IAM_ADMIN_USER.getId());
        LOG.info("#MODEL:    Default client credentials clientId={} clientSecret={}", IAM_ADMIN_CLIENT_CREDENTIALS.getId(), IAM_ADMIN_CLIENT_CREDENTIALS.getSecret());
        ModelImpl model = new ModelImpl(id, name);
        Organization organization = new OrganizationImpl(IAM_ADMINS_ORG, IAM_ADMINS_NAME);
        Project project = new ProjectImpl(IAM_ADMINS_PROJECT, IAM_ADMINS_NAME, organization.getId(), organization.getPrivateKey());
        createAdminRoles().forEach(r-> project.addRole(r));
        createClientRoles().forEach(r-> project.addRole(r));
        assignProjectPermissionsToRoles(project);

        Client client = new Client(IAM_ADMIN_CLIENT_CREDENTIALS, "client-1",3600*1000L, 24*3600*1000L);
        createClientRoles().forEach(r-> client.addRole(r.getId()));
        project.addClient(client);

        User user = new UserImpl(IAM_ADMIN_USER, "iam-admin", project.getId(), 3600*1000L, 24*3600*1000L, project.getPrivateKey());
        UPCredentials upCredentials = new UPCredentials(user.getId(), iamAdminPassword);
        user.addCredentials(upCredentials);
        createAdminRoles().forEach(r -> user.addRole(r.getId()));

        organization.add(project);
        project.add(user);
        model.add(organization);
        return model;
    }

    private static Set<Role> createClientRoles() {
        Role clientReaderRole = new Role(RoleId.from("read-organizations"), "Can read organizations.");
        Set<Role> roles = new HashSet<>();
        roles.add(clientReaderRole);
        return roles;
    }

    private static void assignProjectPermissionsToRoles(Project project) {
        //1. create new permission instances
        Permission readOrganizationsPermission = new Permission(IAM_SERVICE, "organizations", READ_ACTION);
        Permission modifyOrganizationPermission = new Permission(IAM_SERVICE, "organizations", MODIFY_ACTION);
        Permission readProjectsPermission = new Permission(IAM_SERVICE, "projects", READ_ACTION);
        Permission modifyProjectsPermission = new Permission(IAM_SERVICE, "projects", MODIFY_ACTION);
        Permission readUsersRole = new Permission(IAM_SERVICE, "users", READ_ACTION);
        Permission modifyUsersRole =  new Permission(IAM_SERVICE, "users", MODIFY_ACTION);
        Permission readRolePermission = new Permission(IAM_SERVICE, "roles", READ_ACTION);
        Permission modifyRolePermission = new Permission(IAM_SERVICE, "roles", MODIFY_ACTION);
        Permission readPermissionPermission = new Permission(IAM_SERVICE, "permissions", READ_ACTION);
        Permission modifyPermissionPermission = new Permission(IAM_SERVICE, "permissions", MODIFY_ACTION);

        //2. add permissions to project
        project.addPermission(readOrganizationsPermission);
        project.addPermission(modifyOrganizationPermission);
        project.addPermission(readProjectsPermission);
        project.addPermission(modifyProjectsPermission);
        project.addPermission(readUsersRole);
        project.addPermission(modifyUsersRole);
        project.addPermission(readRolePermission);
        project.addPermission(modifyRolePermission);
        project.addPermission(readPermissionPermission);
        project.addPermission(modifyPermissionPermission);

        //3. link roles to permissions
        project.addPermissionToRole(RoleId.from("manage-organizations"), readOrganizationsPermission.getId());
        project.addPermissionToRole(RoleId.from("read-organizations"), readOrganizationsPermission.getId());
        project.addPermissionToRole(RoleId.from("manage-organizations"), modifyOrganizationPermission.getId());
        project.addPermissionToRole(RoleId.from("manage-projects"), readProjectsPermission.getId());
        project.addPermissionToRole(RoleId.from("manage-projects"), modifyProjectsPermission.getId());
        project.addPermissionToRole(RoleId.from("manage-users"), readUsersRole.getId());
        project.addPermissionToRole(RoleId.from("manage-users"), modifyUsersRole.getId());
        project.addPermissionToRole(RoleId.from("manage-roles"), readRolePermission.getId());
        project.addPermissionToRole(RoleId.from("manage-roles"), modifyRolePermission.getId());
        project.addPermissionToRole(RoleId.from("manage-permissions"), readPermissionPermission.getId());
        project.addPermissionToRole(RoleId.from("manage-permissions"), modifyPermissionPermission.getId());
    }

    private static Set<Role> createAdminRoles() {
        Role manageOrganizationsRole = new Role(RoleId.from("manage-organizations"), "Can manage organizations.");
        Role manageProjectsRole = new Role(RoleId.from("manage-projects"), "Can manage projects.");
        Role manageUsersRole = new Role(RoleId.from("manage-users"), "Can manage users.");
        Role manageRolesRole = new Role(RoleId.from("manage-roles"), "Can manage roles.");
        Role managePermissionsRole = new Role(RoleId.from("manage-permissions"), "Can manage permissions.");
        Set<Role> roles = new HashSet<>();
        roles.add(manageOrganizationsRole);
        roles.add(manageProjectsRole);
        roles.add(manageUsersRole);
        roles.add(manageRolesRole);
        roles.add(managePermissionsRole);
        return roles;
    }

    public static OrganizationInfo createOrganizationInfo(Organization organization) throws CertificateEncodingException {
        Set<ProjectId> projects = organization.getProjects().stream().map(project -> project.getId()).collect(Collectors.toSet());
        return new OrganizationInfo(organization.getId(), organization.getName(), projects, organization.getCertificate());
    }

    public static KeyPairSerialized serializeKeyPair(KeyPairData keyPairData) throws PKIException {
        String privateKey = TokenUtils.serializePrivateKey(keyPairData.getPrivateKey());
        String certificate = TokenUtils.serializeX509Certificate(keyPairData.getX509Certificate());
        return new KeyPairSerialized(privateKey, certificate);
    }

    public static KeyPairData deserializeKeyPair(KeyPairSerialized keyPairData) throws PKIException {
        PrivateKey privateKey = TokenUtils.deserializePrivateKey(keyPairData.getPrivateKey());
        X509Certificate certificate = TokenUtils.deserializeX509Certificate(keyPairData.getX509Certificate());
        return new KeyPairData(privateKey, certificate);
    }

    public static String getSha512HashBase64(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hash = md.digest(data.getBytes(Charset.forName("UTF-8")));
        return Base64.toBase64String(hash);
    }

}
