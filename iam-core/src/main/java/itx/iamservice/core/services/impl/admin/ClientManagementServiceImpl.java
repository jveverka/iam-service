package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.ClientImpl;
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
