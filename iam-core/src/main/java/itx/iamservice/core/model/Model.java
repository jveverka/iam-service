package itx.iamservice.core.model;

import java.util.Collection;
import java.util.Optional;

public interface Model {

    void add(Organization organization);

    Collection<Organization> getOrganizations();

    void remove(OrganizationId organizationId);

    Optional<Client> getClient(ClientId clientId);

}
