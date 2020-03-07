package itx.iamservice.services.impl.admin;

import itx.iamservice.model.ModelImpl;
import itx.iamservice.model.Organization;
import itx.iamservice.model.OrganizationId;
import itx.iamservice.services.admin.OrganizationService;
import itx.iamservice.services.dto.CreateOrganizationRequest;
import itx.iamservice.model.ModelUtils;

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
