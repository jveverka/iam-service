package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.services.admin.OrganizationManagerService;

import java.util.Collection;
import java.util.Optional;

public class OrganizationManagerServiceImpl implements OrganizationManagerService {

    private final Model model;

    public OrganizationManagerServiceImpl(Model model) {
        this.model = model;
    }

    @Override
    public boolean create(OrganizationId id, String name) {
        if (model.getOrganization(id).isPresent()) {
            return false;
        } else {
            model.add(new Organization(id, name));
            return true;
        }
    }

    @Override
    public Collection<Organization> getAll() {
        return model.getOrganizations();
    }

    @Override
    public Optional<Organization> get(OrganizationId id) {
        return model.getOrganization(id);
    }

    @Override
    public boolean remove(OrganizationId id) {
        return model.remove(id);
    }

}
