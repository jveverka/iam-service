package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.dto.OrganizationInfo;

import java.util.Collection;
import java.util.Optional;

public interface OrganizationManagerService {

    boolean create(OrganizationId id, String name) throws PKIException;

    Collection<Organization> getAll();

    Collection<OrganizationInfo> getAllInfo();

    Optional<Organization> get(OrganizationId id);

    boolean remove(OrganizationId id);

}
