package one.microproject.iamservice.core;

import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModelCommons {

    private ModelCommons() {
    }

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

    public static final Permission IAM_SERVICE_ORGANIZATIONS_RESOURCE_ACTION_ALL = new Permission(IAM_SERVICE, ORGANIZATIONS_RESOURCE, ACTION_ALL);
    public static final Permission IAM_SERVICE_PROJECTS_RESOURCE_ACTION_ALL      = new Permission(IAM_SERVICE, PROJECTS_RESOURCE,      ACTION_ALL);
    public static final Permission IAM_SERVICE_USERS_RESOURCE_ACTION_ALL         = new Permission(IAM_SERVICE, USERS_RESOURCE,         ACTION_ALL);
    public static final Permission IAM_SERVICE_CLIENTS_RESOURCE_ACTION_ALL       = new Permission(IAM_SERVICE, CLIENTS_RESOURCE,       ACTION_ALL);

    public static final Permission IAM_SERVICE_ORGANIZATION_RESOURCE_ACTION_READ = new Permission(IAM_SERVICE, ORGANIZATION_RESOURCE,  ACTION_READ);

    public static final Set<Permission> ADMIN_ORGANIZATION_SET = Set.of(
            IAM_SERVICE_ORGANIZATIONS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_PROJECTS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_USERS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_CLIENTS_RESOURCE_ACTION_ALL
    );

    public static final Set<Permission> ADMIN_PROJECT_SET = Set.of(
            IAM_SERVICE_ORGANIZATIONS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_USERS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_CLIENTS_RESOURCE_ACTION_ALL
    );

    public static Set<Permission> getOrganizationAdminPermissionSet(OrganizationId organizationId) {
        return Set.of(
                new Permission(getServiceId(organizationId), ORGANIZATIONS_RESOURCE, ACTION_ALL),
                new Permission(getServiceId(organizationId), PROJECTS_RESOURCE, ACTION_ALL),
                new Permission(getServiceId(organizationId), USERS_RESOURCE, ACTION_ALL),
                new Permission(getServiceId(organizationId), CLIENTS_RESOURCE, ACTION_ALL)
        );
    }

    public static Set<Permission> getProjectAdminPermissionSet(OrganizationId organizationId, ProjectId projectId) {
        return Set.of(
                new Permission(getServiceId(organizationId, projectId), ORGANIZATION_RESOURCE, ACTION_ALL),
                new Permission(getServiceId(organizationId, projectId), PROJECT_RESOURCE, ACTION_ALL),
                new Permission(getServiceId(organizationId, projectId), USERS_RESOURCE, ACTION_ALL),
                new Permission(getServiceId(organizationId, projectId), CLIENTS_RESOURCE, ACTION_ALL)
        );
    }

    public static String getServiceId(OrganizationId organizationId) {
        return getServiceId(organizationId, null);
    }

    public static String getServiceId(OrganizationId organizationId, ProjectId projectId) {
        if (projectId == null) {
            return organizationId.getId();
        } else {
            return organizationId.getId() + "/" + projectId.getId();
        }
    }

    /**
     * Create set of admin permissions for organization and project.
     * @param organizationId unique organization ID.
     * @param projectId unique project ID.
     * @return a minimal set of permissions for organization / project admin user.
     */
    public static Set<Permission> createProjectAdminPermissions(OrganizationId organizationId, ProjectId projectId) {
        final Set<Permission> adminPermissions = new HashSet<>();
        adminPermissions.add(new Permission(organizationId.getId() + "-" + projectId.getId(), ORGANIZATIONS_RESOURCE, ACTION_ALL));
        adminPermissions.add(new Permission(organizationId.getId() + "-" + projectId.getId(), PROJECTS_RESOURCE, ACTION_ALL));
        adminPermissions.add(new Permission(organizationId.getId() + "-" + projectId.getId(), USERS_RESOURCE, ACTION_ALL));
        adminPermissions.add(new Permission(organizationId.getId() + "-" + projectId.getId(), CLIENTS_RESOURCE, ACTION_ALL));
        return adminPermissions.stream().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Create unique identifier of project admin role derived from organization / project IDs.
     * @param organizationId unique organization ID.
     * @param projectId unique project ID.
     * @return unique identifier of project admin role.
     */
    public static RoleId createProjectAdminRoleId(OrganizationId organizationId, ProjectId projectId) {
        return RoleId.from(organizationId.getId() + "-" + projectId.getId() + "-admin");
    }

    /**
     * Verify if provided scopes contain sufficient permissions to qualify as project admin.
     * @param organizationId unique organization ID.
     * @param projectId unique project ID.
     * @param scopes set of scopes to verify.
     * @return true if provided set of scopes is qualified as project admin, false otherwise.
     */
    public static boolean verifyProjectAdminPermissions(OrganizationId organizationId, ProjectId projectId, Set<String> scopes) {
        try {
            final Set<Permission> minimalScopeSet = createProjectAdminPermissions(organizationId, projectId);
            final Set<Permission> availableScopes = new HashSet<>();
            for (String scope : scopes) {
                availableScopes.add(Permission.from(scope));
            }
            return availableScopes.containsAll(minimalScopeSet);
        } catch (Exception e) {
            return false;
        }
    }

}
