package itx.iamservice.core.model;

import java.util.Collection;
import java.util.Optional;

public interface Model {

    void add(Organization organization);

    Collection<Organization> getOrganizations();

    Optional<Organization> getOrganization(OrganizationId organizationId);

    boolean remove(OrganizationId organizationId);

    Optional<Client> getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId);

}
