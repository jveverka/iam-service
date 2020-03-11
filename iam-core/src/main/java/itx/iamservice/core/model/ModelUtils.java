package itx.iamservice.core.model;

import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ModelUtils {

    private static final String IAM_ADMINS_NAME = "iam-admins";
    public static final OrganizationId IAM_ADMINS_ORG = OrganizationId.from(IAM_ADMINS_NAME);
    public static final ProjectId IAM_ADMINS_PROJECT = ProjectId.from(IAM_ADMINS_NAME);
    public static final ClientId IAM_ADMIN_CLIENT = ClientId.from("iam-admin-id");

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

    public static ClientId createClientId() {
        return ClientId.from(createId());
    }

    public static RoleId createRoleId() {
        return RoleId.from(createId());
    }

    public static Model createDefaultModel(String iamAdminPassword) throws PKIException {
        ModelImpl model = new ModelImpl();
        Organization organization = new Organization(IAM_ADMINS_ORG, IAM_ADMINS_NAME);
        Project project = new Project(IAM_ADMINS_PROJECT, IAM_ADMINS_NAME, organization.getId(), organization.getPrivateKey());
        createAdminRoles().forEach(r-> project.addRole(r));

        Client client = new Client(IAM_ADMIN_CLIENT, "iam-admin", project.getId(), 3600*1000L, project.getPrivateKey());
        UPCredentials upCredentials = new UPCredentials(client.getId(), iamAdminPassword);
        client.addCredentials(upCredentials);
        createAdminRoles().forEach(r -> client.addRole(r.getId()));

        organization.add(project);
        project.add(client);
        model.add(organization);
        return model;
    }

    private static Set<Role> createAdminRoles() {
        Role manageOrganizationsRole = new Role(RoleId.from("manage-organizations"), "Can manage organizations.");
        manageOrganizationsRole.addPermission(new Permission(IAM_SERVICE, "organizations", READ_ACTION));
        manageOrganizationsRole.addPermission(new Permission(IAM_SERVICE, "organizations", MODIFY_ACTION));

        Role manageProjectsRole = new Role(RoleId.from("manage-projects"), "Can manage projects.");
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "projects", READ_ACTION));
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "projects", MODIFY_ACTION));

        Role manageClientsRole = new Role(RoleId.from("manage-clients"), "Can manage clients.");
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "clients", READ_ACTION));
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "clients", MODIFY_ACTION));

        Role manageRolesRole = new Role(RoleId.from("manage-roles"), "Can manage roles.");
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "roles", READ_ACTION));
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "roles", MODIFY_ACTION));

        Role managePermissionsRole = new Role(RoleId.from("manage-permissions"), "Can manage permissions.");
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "permissions", READ_ACTION));
        manageProjectsRole.addPermission(new Permission(IAM_SERVICE, "permissions", MODIFY_ACTION));

        Set<Role> roles = new HashSet<>();
        roles.add(manageOrganizationsRole);
        roles.add(manageProjectsRole);
        roles.add(manageClientsRole);
        roles.add(manageRolesRole);
        roles.add(managePermissionsRole);
        return roles;
    }

}
