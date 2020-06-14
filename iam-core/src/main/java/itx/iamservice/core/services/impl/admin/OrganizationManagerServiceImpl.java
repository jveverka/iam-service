package itx.iamservice.core.services.impl.admin;

import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.OrganizationImpl;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.OrganizationInfo;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrganizationManagerServiceImpl implements OrganizationManagerService {

    private final ModelCache modelCache;

    public OrganizationManagerServiceImpl(ModelCache modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public boolean create(OrganizationId id, CreateOrganizationRequest createOrganizationRequest) throws PKIException {
        if (modelCache.getOrganization(id).isPresent()) {
            return false;
        } else {
            modelCache.add(new OrganizationImpl(id, createOrganizationRequest.getName()));
            return true;
        }
    }

    @Override
    public Optional<OrganizationId> create(CreateOrganizationRequest request) throws PKIException {
        if(create(request.getId(), request)) {
            return Optional.of(request.getId());
        } else {
            return Optional.empty();
        }
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

}
