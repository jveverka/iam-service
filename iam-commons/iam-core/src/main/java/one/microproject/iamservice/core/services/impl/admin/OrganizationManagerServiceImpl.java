package one.microproject.iamservice.core.services.impl.admin;

import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.OrganizationImpl;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.services.admin.OrganizationManagerService;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateOrganizationRequest;
import one.microproject.iamservice.core.services.dto.OrganizationInfo;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class OrganizationManagerServiceImpl implements OrganizationManagerService {

    private final ModelCache modelCache;

    public OrganizationManagerServiceImpl(ModelCache modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public Optional<OrganizationId> create(CreateOrganizationRequest request) throws PKIException {
        return modelCache.add(new OrganizationImpl(request.getId(), request.getName()));
    }

    @Override
    public Collection<Organization> getAll() {
        return modelCache.getOrganizations();
    }

    @Override
    public Collection<OrganizationInfo> getAllInfo() throws CertificateEncodingException  {
        List<OrganizationInfo> organizationInfoList = new ArrayList<>();
        for (Organization organization: getAll()) {
            organizationInfoList.add(ModelUtils.createOrganizationInfo(organization));
        }
        return organizationInfoList;
    }

    @Override
    public Optional<Organization> get(OrganizationId id) {
        return modelCache.getOrganization(id);
    }

    @Override
    public Optional<OrganizationInfo> getInfo(OrganizationId id) throws CertificateEncodingException {
        Optional<Organization> organization = modelCache.getOrganization(id);
        if (organization.isPresent()) {
            return Optional.of(ModelUtils.createOrganizationInfo(organization.get()));
        }
        return Optional.empty();
    }

    @Override
    public boolean remove(OrganizationId id) {
        return modelCache.remove(id);
    }

    @Override
    public boolean removeWithDependencies(OrganizationId organizationId) {
        return modelCache.removeWithDependencies(organizationId);
    }

    @Override
    public void setProperty(OrganizationId id, String key, String value) {
        modelCache.setProperty(id, key, value);
    }

    @Override
    public void removeProperty(OrganizationId id, String key) {
        modelCache.removeProperty(id, key);
    }

    @Override
    public Collection<User> getAllUsers(OrganizationId id) {
        return modelCache.getUsers(id);
    }

}
