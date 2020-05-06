package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.admin.ClientManagementService;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.CreateClientRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class ClientManagementServiceImpl implements ClientManagementService {

    private final ModelCache modelCache;

    public ClientManagementServiceImpl(ModelCache modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public Optional<ClientId> createClient(OrganizationId id, ProjectId projectId, CreateClientRequest createProjectRequest) {
        Optional<Project> projectOptional = getProject(id, projectId);
        if (projectOptional.isPresent()) {
            ClientId clientId = ClientId.from(UUID.randomUUID().toString());
            ClientCredentials credentials = new ClientCredentials(clientId, UUID.randomUUID().toString());
            Client client = new Client(credentials, createProjectRequest.getName(),
                    createProjectRequest.getDefaultAccessTokenDuration(), createProjectRequest.getDefaultRefreshTokenDuration());
            modelCache.add(id, projectId, client);
            return Optional.of(client.getId());
        }
        return Optional.empty();
    }

    @Override
    public Collection<Client> getClients(OrganizationId id, ProjectId projectId) {
        return modelCache.getClients(id, projectId);
    }

    @Override
    public Optional<Client> getClient(OrganizationId id, ProjectId projectId, ClientId clientId) {
        return modelCache.getClient(id, projectId, clientId);
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
            Optional<Client> client = modelCache.getClient(id, projectId, clientId);
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
            Optional<Client> client = modelCache.getClient(id, projectId, clientId);
            if (client.isPresent()) {
                return client.get().removeRole(roleId);
            }
        }
        return false;
    }

    private Optional<Project> getProject(OrganizationId id, ProjectId projectId) {
        Optional<Organization> organization = modelCache.getOrganization(id);
        if (organization.isPresent()) {
            return modelCache.getProject(id, projectId);
        }
        return Optional.empty();
    }

}
