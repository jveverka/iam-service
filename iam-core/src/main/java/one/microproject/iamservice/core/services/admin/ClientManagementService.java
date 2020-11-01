package one.microproject.iamservice.core.services.admin;

import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.dto.CreateClientRequest;

import java.util.Collection;
import java.util.Optional;

public interface ClientManagementService {

    Optional<ClientCredentials> createClient(OrganizationId id, ProjectId projectId, CreateClientRequest createProjectRequest);

    Collection<Client> getClients(OrganizationId id, ProjectId projectId);

    Optional<Client> getClient(OrganizationId id, ProjectId projectId, ClientId clientId);

    boolean removeClient(OrganizationId id, ProjectId projectId, ClientId clientId);

    boolean addRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId);

    boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId);

}
