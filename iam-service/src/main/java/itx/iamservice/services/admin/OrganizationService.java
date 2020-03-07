package itx.iamservice.services.admin;

import itx.iamservice.model.Organization;
import itx.iamservice.model.OrganizationId;
import itx.iamservice.services.dto.CreateOrganizationRequest;

import java.util.Collection;

public interface OrganizationService {

    OrganizationId createOrganization(CreateOrganizationRequest createOrganizationRequest);

    Collection<Organization> getAll();

    void remove(OrganizationId organizationId);

}
