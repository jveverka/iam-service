package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.admin.ProjectManagerService;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ProjectManagerServiceImpl implements ProjectManagerService {

    private final Model model;

    public ProjectManagerServiceImpl(Model model) {
        this.model = model;
    }

    @Override
    public boolean create(OrganizationId id, ProjectId projectId, String name) throws PKIException {
        Optional<Organization> organization = model.getOrganization(id);
        if (organization.isPresent()) {
            Optional<Project> project = organization.get().getProject(projectId);
            if (project.isEmpty()) {
                organization.get().add(new Project(projectId, name, id, organization.get().getPrivateKey()));
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<Project> getAll(OrganizationId id) {
        Optional<Organization> organization = model.getOrganization(id);
        if (organization.isPresent()) {
            return organization.get().getProjects();
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Project> get(OrganizationId id, ProjectId projectId) {
        Optional<Organization> organization = model.getOrganization(id);
        if (organization.isPresent()) {
            return organization.get().getProject(projectId);
        }
        return Optional.empty();
    }

    @Override
    public boolean remove(OrganizationId id, ProjectId projectId) {
        Optional<Organization> organization = model.getOrganization(id);
        if (organization.isPresent()) {
            return organization.get().remove(projectId);
        }
        return false;
    }

    @Override
    public boolean addRole(OrganizationId id, ProjectId projectId, Role role) {
        Optional<Organization> organization = model.getOrganization(id);
        if (organization.isPresent()) {
            Optional<Project> project = organization.get().getProject(projectId);
            if (project.isPresent()) {
                project.get().addRole(role);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, RoleId roleId) {
        Optional<Organization> organization = model.getOrganization(id);
        if (organization.isPresent()) {
            Optional<Project> project = organization.get().getProject(projectId);
            if (project.isPresent()) {
                return project.get().removeRole(roleId);
            }
        }
        return false;
    }

    @Override
    public Collection<Role> getRoles(OrganizationId id, ProjectId projectId) {
        Optional<Organization> organization = model.getOrganization(id);
        if (organization.isPresent()) {
            Optional<Project> project = organization.get().getProject(projectId);
            if (project.isPresent()) {
                return project.get().getRoles();
            }
        }
        return Collections.emptyList();
    }

}
