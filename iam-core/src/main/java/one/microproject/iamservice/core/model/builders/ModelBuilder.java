package one.microproject.iamservice.core.model.builders;

import one.microproject.iamservice.core.model.Model;
import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.ModelImpl;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.OrganizationImpl;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.persistence.PersistenceService;

import java.util.UUID;

public final class ModelBuilder {

    private final ModelCache modelCache;

    public ModelBuilder(ModelId id, String name, PersistenceService persistenceService) {
        Model model = new ModelImpl(id, name);
        this.modelCache = new ModelCacheImpl(model, persistenceService);
    }

    public ModelBuilder(String name, PersistenceService persistenceService) {
        ModelId id = ModelId.from(UUID.randomUUID().toString());
        Model model = new ModelImpl(id, name);
        this.modelCache = new ModelCacheImpl(model, persistenceService);
    }

    public OrganizationBuilder addOrganization(String name) throws PKIException {
        OrganizationId id = OrganizationId.from(UUID.randomUUID().toString());
        return addOrganization(id, name);
    }

    public OrganizationBuilder addOrganization(OrganizationId id, String name) throws PKIException {
        Organization organization = new OrganizationImpl(id, name);
        this.modelCache.add(organization);
        return new OrganizationBuilder(modelCache,this, organization);
    }

    public ModelCache build() {
        return modelCache;
    }

    public static ModelBuilder builder(String name, PersistenceService persistenceService) {
        return new ModelBuilder(name, persistenceService);
    }

    public static ModelBuilder builder(ModelId id, String name, PersistenceService persistenceService) {
        return new ModelBuilder(id, name, persistenceService);
    }

}
