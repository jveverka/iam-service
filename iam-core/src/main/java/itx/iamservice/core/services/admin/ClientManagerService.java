package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ClientManagerService {

    boolean create(OrganizationId id, ProjectId projectId, ClientId clientId, String name) throws PKIException;

    Collection<Client> getAll(OrganizationId id, ProjectId projectId);

    Optional<Client> get(OrganizationId id, ProjectId projectId, ClientId clientId);

    boolean remove(OrganizationId id, ProjectId projectId, ClientId clientId);

    boolean assignRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId);

    boolean removeRole(OrganizationId id, ProjectId projectId, ClientId clientId, RoleId roleId);

    Set<RoleId> getRoles(OrganizationId id, ProjectId projectId, ClientId clientId);

}
