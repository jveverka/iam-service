package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.admin.ClientManagementService;
import itx.iamservice.core.services.dto.CreateClientRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class ClientManagementServiceImpl implements ClientManagementService {

    private final Model model;

    public ClientManagementServiceImpl(Model model) {
        this.model = model;
    }

    @Override
    public Optional<ClientId> createClient(OrganizationId id, ProjectId projectId, CreateClientRequest createProjectRequest) {
        Optional<Project> projectOptional = getProject(id, projectId);
        if (projectOptional.isPresent()) {
            ClientId clientId = ClientId.from(UUID.randomUUID().toString());
            ClientCredentials credentials = new ClientCredentials(clientId, UUID.randomUUID().toString());
            Client client = new Client(credentials, createProjectRequest.getName(),
                    createProjectRequest.getDefaultAccessTokenDuration(), createProjectRequest.getDefaultRefreshTokenDuration());
            projectOptional.get().addClient(client);
            return Optional.of(client.getId());
        }
        return Optional.empty();
    }

    @Override
    public Collection<Client> getClients(OrganizationId id, ProjectId projectId) {
        Optional<Project> projectOptional = getProject(id, projectId);
        if (projectOptional.isPresent()) {
            return projectOptional.get().getClients();
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Client> getClient(OrganizationId id, ProjectId projectId, ClientId clientId) {
        Optional<Project> projectOptional = getProject(id, projectId);
        if (projectOptional.isPresent()) {
            return projectOptional.get().getClient(clientId);
        }
        return Optional.empty();
    }

    @Override
    public boolean removeClient(OrganizationId id, ProjectId projectId, ClientId clientId) {
        Optional<Project> projectOptional = getProject(id, projectId);
        if (projectOptional.isPresent()) {
            return projectOptional.get().removeClient(clientId);
        }
        return false;
    }

    @Override
    public boolean addRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        Optional<Project> projectOptional = getProject(id, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> client = projectOptional.get().getClient(clientId);
            if (client.isPresent()) {
                return client.get().addRole(roleId);
            }
        }
        return false;
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        Optional<Project> projectOptional = getProject(id, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> client = projectOptional.get().getClient(clientId);
            if (client.isPresent()) {
                return client.get().removeRole(roleId);
            }
        }
        return false;
    }

    private Optional<Project> getProject(OrganizationId id, ProjectId projectId) {
        Optional<Organization> organization = model.getOrganization(id);
        if (organization.isPresent()) {
            return organization.get().getProject(projectId);
        }
        return Optional.empty();
    }

}
