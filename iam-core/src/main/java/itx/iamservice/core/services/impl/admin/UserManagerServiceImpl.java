package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Credentials;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserImpl;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.dto.CreateUserRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserManagerServiceImpl implements UserManagerService {

    private final Model model;

    public UserManagerServiceImpl(Model model) {
        this.model = model;
    }

    @Override
    public boolean create(OrganizationId id, ProjectId projectId, UserId userId, String name) throws PKIException {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<User> userOptional = projectOptional.get().getUser(userId);
                if (userOptional.isEmpty()) {
                    projectOptional.get().add(new UserImpl(userId, name, projectOptional.get().getId(), 3600*1000L, 24*3600*1000L, projectOptional.get().getPrivateKey()));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Optional<UserId> create(OrganizationId id, ProjectId projectId, CreateUserRequest request) throws PKIException {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                UserId userId = UserId.from(UUID.randomUUID().toString());
                projectOptional.get().add(new UserImpl(userId, request.getName(), projectOptional.get().getId(),
                      request.getDefaultAccessTokenDuration(), request.getDefaultRefreshTokenDuration(), projectOptional.get().getPrivateKey()));
                return Optional.of(userId);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> getAll(OrganizationId id, ProjectId projectId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                return projectOptional.get().getAllUsers();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<User> get(OrganizationId id, ProjectId projectId, UserId userId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                return projectOptional.get().getUser(userId);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean remove(OrganizationId id, ProjectId projectId, UserId userId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                return projectOptional.get().remove(userId);
            }
        }
        return false;
    }

    @Override
    public boolean assignRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<User> userOptional = projectOptional.get().getUser(userId);
                if (userOptional.isPresent()) {
                    userOptional.get().addRole(roleId);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, UserId userId, RoleId roleId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<User> userOptional = projectOptional.get().getUser(userId);
                if (userOptional.isPresent()) {
                    userOptional.get().removeRole(roleId);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<RoleId> getRoles(OrganizationId id, ProjectId projectId, UserId userId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<User> userOptional = projectOptional.get().getUser(userId);
                if (userOptional.isPresent()) {
                    return userOptional.get().getRoles();
                }
            }
        }
        return Collections.emptySet();
    }

    @Override
    public boolean setCredentials(OrganizationId id, ProjectId projectId, UserId userId, Credentials credentials) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<User> userOptional = projectOptional.get().getUser(userId);
                if (userOptional.isPresent()) {
                    userOptional.get().addCredentials(credentials);
                    return true;
                }
            }
        }
        return false;
    }

}
