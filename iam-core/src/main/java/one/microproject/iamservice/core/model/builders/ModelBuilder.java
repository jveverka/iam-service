package one.microproject.iamservice.core.model.builders;

import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.OrganizationImpl;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;

import java.util.UUID;

public final class ModelBuilder {

    private final ModelCache modelCache;

    public ModelBuilder(ModelWrapper modelWrapper) {
        this.modelCache = new ModelCacheImpl(modelWrapper);
    }

    public OrganizationBuilder addOrganization(String name) throws PKIException {
        OrganizationId id = OrganizationId.from(UUID.randomUUID().toString());
        return addOrganization(id, name);
    }

    public OrganizationBuilder addOrganization(OrganizationId id, String name) throws PKIException {
        Organization organization = new OrganizationImpl(id, name);
        this.modelCache.add(organization);
        return new OrganizationBuilder(modelCache, this, organization.getId());
    }

    public ModelCache build() {
        return modelCache;
    }

    public static ModelBuilder builder(ModelWrapper modelWrapper) {
        return new ModelBuilder(modelWrapper);
    }

}
