package one.microproject.iamservice.core.services.impl.admin;

import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.admin.ClientManagementService;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateClientRequest;

import java.util.Collection;
import java.util.Optional;

public class ClientManagementServiceImpl implements ClientManagementService {

    private final ModelCache modelCache;

    public ClientManagementServiceImpl(ModelCache modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public Optional<ClientCredentials> createClient(OrganizationId id, ProjectId projectId, CreateClientRequest request) {
        Optional<Client> client = modelCache.add(id, projectId, request);
        if (client.isPresent()) {
            return Optional.of(client.get().getCredentials());
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
        return modelCache.remove(id, projectId, clientId);
    }

    @Override
    public boolean addRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        return modelCache.assignRole(id, projectId, clientId, roleId);
    }

    @Override
    public boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId) {
        return modelCache.removeRole(id, projectId, clientId, roleId);
    }

}
