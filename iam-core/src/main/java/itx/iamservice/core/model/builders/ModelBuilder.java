package itx.iamservice.core.model.builders;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.ModelImpl;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.OrganizationImpl;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.impl.caches.ModelCacheImpl;
import itx.iamservice.core.services.persistence.PersistenceService;

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
        return new OrganizationBuilder(this, organization);
    }

    public void addProject(OrganizationId id, Project project) {
        this.modelCache.add(id, project);
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
