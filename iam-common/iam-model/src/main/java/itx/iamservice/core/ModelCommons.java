package itx.iamservice.core;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;

import java.util.Set;

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

}
