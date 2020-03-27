package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.dto.CreateClientRequest;

import java.util.Collection;
import java.util.Optional;

public interface ClientManagementService {

    Optional<ClientId> createClient(OrganizationId id, ProjectId projectId, CreateClientRequest createProjectRequest);

    Collection<Client> getClients(OrganizationId id, ProjectId projectId);

    Optional<Client> getClient(OrganizationId id, ProjectId projectId, ClientId clientId);

    boolean removeClient(OrganizationId id, ProjectId projectId, ClientId clientId);

    boolean addRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId);

    boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId);

}
