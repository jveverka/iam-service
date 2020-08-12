package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.RoleImpl;
import itx.iamservice.core.model.User;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;

import java.util.Collection;
import java.util.Optional;

public class ProjectManagerServiceImpl implements ProjectManagerService {

    private final ModelCache modelCache;

    public ProjectManagerServiceImpl(ModelCache modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public Optional<Project> create(OrganizationId id, CreateProjectRequest request) throws PKIException {
        return modelCache.add(id, request);
    }

    @Override
    public Collection<Project> getAll(OrganizationId id) {
        return modelCache.getProjects(id);
    }

    @Override
    public Collection<User> getUsers(OrganizationId id, ProjectId projectId) {
        return modelCache.getUsers(id, projectId);
    }

    @Override
    public Optional<Project> get(OrganizationId id, ProjectId projectId) {
        return modelCache.getProject(id, projectId);
    }

    @Override
    public boolean remove(OrganizationId id, ProjectId projectId) {
        return modelCache.remove(id, projectId);
    }

    @Override
    public Optional<RoleId> addRole(OrganizationId id, ProjectId projectId, CreateRoleRequest request) {
        Role role = new RoleImpl(request.getId(), request.getName());
        return modelCache.add(id, projectId, role);
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, RoleId roleId) {
        return modelCache.remove(id,  projectId, roleId);
    }

    @Override
    public Collection<Role> getRoles(OrganizationId id, ProjectId projectId) {
        return modelCache.getRoles(id, projectId);
    }

    @Override
    public void addPermission(OrganizationId id, ProjectId projectId, Permission permission) {
        modelCache.addPermission(id, projectId, permission);
    }

    @Override
    public Collection<Permission> getPermissions(OrganizationId id, ProjectId projectId) {
        return modelCache.getPermissions(id, projectId);
    }

    @Override
    public boolean removePermission(OrganizationId id, ProjectId projectId, PermissionId permissionId) {
        return modelCache.removePermission(id, projectId, permissionId);
    }

    @Override
    public boolean addPermissionToRole(OrganizationId id, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        return modelCache.addPermissionToRole(id, projectId, roleId, permissionId);
    }

    @Override
    public boolean removePermissionFromRole(OrganizationId id, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        return modelCache.removePermissionFromRole(id, projectId, roleId, permissionId);
    }

    @Override
    public void setProperty(OrganizationId id, ProjectId projectId, String key, String value) {
        modelCache.setProperty(id, projectId, key, value);
    }

    @Override
    public void removeProperty(OrganizationId id, ProjectId projectId, String key) {
        modelCache.removeProperty(id, projectId, key);
    }

}
