package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Credentials;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserImpl;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.CreateUserRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserManagerServiceImpl implements UserManagerService {

    private final ModelCache modelCache;

    public UserManagerServiceImpl(ModelCache modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public boolean create(OrganizationId id, ProjectId projectId, UserId userId, String name) throws PKIException {
        Optional<Project> projectOptional = modelCache.getProject(id, projectId);
        if (projectOptional.isPresent()) {
            Optional<User> userOptional = modelCache.getUser(id, projectId, userId);
            if (userOptional.isEmpty()) {
                User user = new UserImpl(userId, name, projectOptional.get().getId(), 3600*1000L, 24*3600*1000L, projectOptional.get().getPrivateKey());
                modelCache.add(id, projectId, user);
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<UserId> create(OrganizationId id, ProjectId projectId, CreateUserRequest request) throws PKIException {
        Optional<Project> projectOptional = modelCache.getProject(id, projectId);
        Optional<User> userOptional = modelCache.getUser(id, projectId, request.getId());
        if (projectOptional.isPresent() && userOptional.isEmpty()) {
            User user = new UserImpl(request.getId(), request.getName(), projectOptional.get().getId(),
                    request.getDefaultAccessTokenDuration(), request.getDefaultRefreshTokenDuration(), projectOptional.get().getPrivateKey());
            modelCache.add(id, projectId, user);
            return Optional.of(request.getId());
        }
        return Optional.empty();
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
        Optional<User> userOptional = modelCache.getUser(id, projectId, userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getRoles();
        }
        return Collections.emptySet();
    }

    @Override
    public boolean setCredentials(OrganizationId id, ProjectId projectId, UserId userId, Credentials credentials) {
        Optional<User> userOptional = modelCache.getUser(id, projectId, userId);
        if (userOptional.isPresent()) {
            userOptional.get().addCredentials(credentials);
            return true;
        }
        return false;
    }

}
