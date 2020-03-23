package itx.iamservice.core.model.utils;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;
import itx.iamservice.core.services.dto.OrganizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        LOG.info("#MODEL: Initializing default model ...");
        LOG.info("#MODEL: Default organizationId={}, projectId={}", IAM_ADMINS_ORG.getId(), IAM_ADMINS_PROJECT.getId());
        LOG.info("#MODEL:    Default admin userId={}", IAM_ADMIN_USER.getId());
        LOG.info("#MODEL:    Default client credentials clientId={} clientSecret={}", IAM_ADMIN_CLIENT_CREDENTIALS.getId(), IAM_ADMIN_CLIENT_CREDENTIALS.getSecret());
        ModelImpl model = new ModelImpl();
        Organization organization = new Organization(IAM_ADMINS_ORG, IAM_ADMINS_NAME);
        Project project = new Project(IAM_ADMINS_PROJECT, IAM_ADMINS_NAME, organization.getId(), organization.getPrivateKey());
        createAdminRoles().forEach(r-> project.addRole(r));
        createClientRoles().forEach(r-> project.addRole(r));
        Client client = new Client(IAM_ADMIN_CLIENT_CREDENTIALS, 3600*1000L, 24*3600*1000L);
        createClientRoles().forEach(r-> client.addRole(r.getId()));
        project.addClient(client);

        User user = new User(IAM_ADMIN_USER, "iam-admin", project.getId(), 3600*1000L, 24*3600*1000L, project.getPrivateKey());
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
        clientReaderRole.addPermission(new Permission(IAM_SERVICE, "organizations", READ_ACTION));

        Set<Role> roles = new HashSet<>();
        roles.add(clientReaderRole);
        return roles;
    }

    private static Set<Role> createAdminRoles() {
        Role manageOrganizationsRole = new Role(RoleId.from("manage-organizations"), "Can manage organizations.");
        manageOrganizationsRole.addPermission(new Permission(IAM_SERVICE, "organizations", READ_ACTION));
        manageOrganizationsRole.addPermission(new Permission(IAM_SERVICE, "organizations", MODIFY_ACTION));

        Role manageProjectsRole = new Role(RoleId.from("manage-projects"), "Can manage projects.");
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "projects", READ_ACTION));
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "projects", MODIFY_ACTION));

        Role manageUsersRole = new Role(RoleId.from("manage-users"), "Can manage users.");
        manageUsersRole.addPermission(new Permission(IAM_SERVICE, "users", READ_ACTION));
        manageUsersRole.addPermission(new Permission(IAM_SERVICE, "users", MODIFY_ACTION));

        Role manageRolesRole = new Role(RoleId.from("manage-roles"), "Can manage roles.");
        manageRolesRole.addPermission(new Permission(IAM_SERVICE, "roles", READ_ACTION));
        manageRolesRole.addPermission(new Permission(IAM_SERVICE, "roles", MODIFY_ACTION));

        Role managePermissionsRole = new Role(RoleId.from("manage-permissions"), "Can manage permissions.");
        managePermissionsRole.addPermission(new Permission(IAM_SERVICE, "permissions", READ_ACTION));
        managePermissionsRole.addPermission(new Permission(IAM_SERVICE, "permissions", MODIFY_ACTION));

        Set<Role> roles = new HashSet<>();
        roles.add(manageOrganizationsRole);
        roles.add(manageProjectsRole);
        roles.add(manageUsersRole);
        roles.add(manageRolesRole);
        roles.add(managePermissionsRole);
        return roles;
    }

    public static OrganizationInfo createOrganizationInfo(Organization organization) {
        Set<ProjectId> projects = organization.getProjects().stream().map(project -> project.getId()).collect(Collectors.toSet());
        return new OrganizationInfo(organization.getId(), organization.getName(), projects, organization.getCertificate());
    }

}
