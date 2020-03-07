package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.ModelUtils;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.services.admin.OrganizationService;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;

import java.util.Collection;

public class OrganizationServiceImpl implements OrganizationService {

    private final ModelImpl model;

    public OrganizationServiceImpl(ModelImpl model) {
        this.model = model;
    }

    @Override
    public OrganizationId createOrganization(CreateOrganizationRequest createOrganizationRequest) {
        OrganizationId organizationId = ModelUtils.createOrganizationId();
        model.add(new Organization(organizationId, createOrganizationRequest.getName(), model));
        return organizationId;
    }

    @Override
    public Collection<Organization> getAll() {
        return model.getOrganizations();
    }

    @Override
    public void remove(OrganizationId organizationId) {
        model.remove(organizationId);
    }

}
