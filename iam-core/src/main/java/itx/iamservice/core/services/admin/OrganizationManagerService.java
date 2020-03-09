package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;

import java.util.Collection;
import java.util.Optional;

public interface OrganizationManagerService {

    boolean create(OrganizationId id, String name);

    Collection<Organization> getAll();

    Optional<Organization> get(OrganizationId id);

    boolean remove(OrganizationId id);

}
