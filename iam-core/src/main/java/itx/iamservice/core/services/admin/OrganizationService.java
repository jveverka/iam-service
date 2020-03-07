package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;

import java.util.Collection;

public interface OrganizationService {

    OrganizationId createOrganization(CreateOrganizationRequest createOrganizationRequest);

    Collection<Organization> getAll();

    void remove(OrganizationId organizationId);

}
