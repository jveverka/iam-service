package one.microproject.iamservice.core.services.admin;

import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.PermissionId;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.services.dto.CreateProjectRequest;
import one.microproject.iamservice.core.services.dto.CreateRoleRequest;

import java.util.Collection;
import java.util.Optional;

public interface ProjectManagerService {

    Optional<Project> create(OrganizationId id, CreateProjectRequest createProjectRequest) throws PKIException;

    Collection<Project> getAll(OrganizationId id);

    Collection<User> getUsers(OrganizationId id, ProjectId projectId);

    Optional<Project> get(OrganizationId id, ProjectId projectId);

    boolean remove(OrganizationId id, ProjectId projectId);

    boolean removeWithDependencies(OrganizationId id, ProjectId projectId);

    Optional<RoleId> addRole(OrganizationId id, ProjectId projectId, CreateRoleRequest createRoleRequest);

    boolean removeRole(OrganizationId id, ProjectId projectId, RoleId roleId);

    Collection<Role> getRoles(OrganizationId id, ProjectId projectId);

    void addPermission(OrganizationId id, ProjectId projectId, Permission permission);

    Collection<Permission> getPermissions(OrganizationId id, ProjectId projectId);

    boolean removePermission(OrganizationId id, ProjectId projectId, PermissionId permissionId);

    boolean addPermissionToRole(OrganizationId id, ProjectId projectId, RoleId roleId, PermissionId permissionId);

    boolean removePermissionFromRole(OrganizationId id, ProjectId projectId, RoleId roleId, PermissionId permissionId);

    void setProperty(OrganizationId id, ProjectId projectId, String key, String value);

    void removeProperty(OrganizationId id, ProjectId projectId, String key);

}
