package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.admin.UserManagerService;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

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
                    projectOptional.get().add(new User(userId, name, projectOptional.get().getId(), 3600*1000L, projectOptional.get().getPrivateKey()));
                    return true;
                }
            }
        }
        return false;
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

}
