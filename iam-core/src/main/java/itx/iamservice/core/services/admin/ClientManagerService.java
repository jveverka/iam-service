package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.util.Collection;
import java.util.Optional;

public interface ClientManagerService {

    boolean create(OrganizationId id, ProjectId projectId, ClientId clientId, String name);

    Collection<Client> getAll(OrganizationId id, ClientId clientId);

    Optional<Client> get(OrganizationId id, ProjectId projectId, ClientId clientId);

    boolean remove(OrganizationId id, ProjectId projectId, ClientId clientId);

}
