package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.dto.OrganizationInfo;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrganizationManagerServiceImpl implements OrganizationManagerService {

    private final Model model;

    public OrganizationManagerServiceImpl(Model model) {
        this.model = model;
    }

    @Override
    public boolean create(OrganizationId id, String name) throws PKIException {
        if (model.getOrganization(id).isPresent()) {
            return false;
        } else {
            model.add(new Organization(id, name));
            return true;
        }
    }

    @Override
    public Optional<OrganizationId> create(String name) throws PKIException {
        OrganizationId id = OrganizationId.from(UUID.randomUUID().toString());
        if(create(id, name)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Organization> getAll() {
        return model.getOrganizations();
    }

    @Override
    public Collection<OrganizationInfo> getAllInfo() {
        return getAll().stream().map(o -> ModelUtils.createOrganizationInfo(o)).collect(Collectors.toList());
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
