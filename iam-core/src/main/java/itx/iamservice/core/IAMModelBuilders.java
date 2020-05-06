package itx.iamservice.core;

import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.builders.ModelBuilder;
import itx.iamservice.core.model.builders.RoleBuilder;
import itx.iamservice.core.services.impl.persistence.InMemoryPersistenceServiceImpl;
import itx.iamservice.core.services.persistence.PersistenceService;

public final class IAMModelBuilders {

    private IAMModelBuilders() {
    }

    public static ModelBuilder modelBuilder(String name, PersistenceService persistenceService) {
        return new ModelBuilder(name, persistenceService);
    }

    public static ModelBuilder modelBuilder(String name) {
        return new ModelBuilder(name, new InMemoryPersistenceServiceImpl());
    }

    public static ModelBuilder modelBuilder(ModelId id, String name, PersistenceService persistenceService) {
        return new ModelBuilder(id, name, persistenceService);
    }

    public static ModelBuilder modelBuilder(ModelId id, String name) {
        return new ModelBuilder(id, name, new InMemoryPersistenceServiceImpl());
    }

    public static RoleBuilder roleBuilder(String name) {
        return new RoleBuilder(name);
    }

    public static RoleBuilder roleBuilder(RoleId id, String name) {
        return new RoleBuilder(id, name);
    }

}
