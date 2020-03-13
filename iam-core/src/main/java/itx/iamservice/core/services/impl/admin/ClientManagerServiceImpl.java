package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.admin.ClientManagerService;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class ClientManagerServiceImpl implements ClientManagerService {

    private final Model model;

    public ClientManagerServiceImpl(Model model) {
        this.model = model;
    }

    @Override
    public boolean create(OrganizationId id, ProjectId projectId, ClientId clientId, String name) throws PKIException {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<Client> client = projectOptional.get().getClient(clientId);
                if (client.isEmpty()) {
                    projectOptional.get().add(new Client(clientId, name, projectOptional.get().getId(), 3600*1000L, projectOptional.get().getPrivateKey()));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Collection<Client> getAll(OrganizationId id, ProjectId projectId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                return projectOptional.get().getAllClients();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Client> get(OrganizationId id, ProjectId projectId, ClientId clientId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                return projectOptional.get().getClient(clientId);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean remove(OrganizationId id, ProjectId projectId, ClientId clientId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                return projectOptional.get().remove(clientId);
            }
        }
        return false;
    }

    @Override
    public boolean assignRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<Client> clientOptional = projectOptional.get().getClient(clientId);
                if (clientOptional.isPresent()) {
                    clientOptional.get().addRole(roleId);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<Client> clientOptional = projectOptional.get().getClient(clientId);
                if (clientOptional.isPresent()) {
                    clientOptional.get().removeRole(roleId);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<RoleId> getRoles(OrganizationId id, ProjectId projectId, ClientId clientId) {
        Optional<Organization> organizationOptional = model.getOrganization(id);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = organizationOptional.get().getProject(projectId);
            if (projectOptional.isPresent()) {
                Optional<Client> clientOptional = projectOptional.get().getClient(clientId);
                if (clientOptional.isPresent()) {
                    return clientOptional.get().getRoles();
                }
            }
        }
        return Collections.emptySet();
    }

}
