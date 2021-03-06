package one.microproject.iamservice.core.services.impl.admin;

import one.microproject.iamservice.core.model.Credentials;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.admin.UserManagerService;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateUserRequest;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class UserManagerServiceImpl implements UserManagerService {

    private final ModelCache modelCache;

    public UserManagerServiceImpl(ModelCache modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public Optional<User> create(OrganizationId id, ProjectId projectId, CreateUserRequest request) throws PKIException {
        return modelCache.add(id, projectId, request);
    }

    @Override
    public Collection<User> getAll(OrganizationId id, ProjectId projectId) {
        return modelCache.getUsers(id, projectId);
    }

    @Override
    public Optional<User> get(OrganizationId id, ProjectId projectId, UserId userId) {
        return modelCache.getUser(id, projectId, userId);
    }

    @Override
    public boolean remove(OrganizationId id, ProjectId projectId, UserId userId) {
        return modelCache.remove(id, projectId, userId);
    }

    @Override
    public boolean assignRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId) {
        return modelCache.assignRole(id, projectId, userId, roleId);
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId) {
        return modelCache.removeRole(id, projectId, userId, roleId);
    }

    @Override
    public Set<RoleId> getRoles(OrganizationId id, ProjectId projectId, UserId userId) {
        return modelCache.getRoles(id, projectId, userId);
    }

    @Override
    public boolean setCredentials(OrganizationId id, ProjectId projectId, UserId userId, Credentials credentials) {
        return modelCache.setCredentials(id, projectId, userId, credentials);
    }

}
