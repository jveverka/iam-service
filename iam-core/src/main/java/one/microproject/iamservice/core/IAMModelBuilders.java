package one.microproject.iamservice.core;

import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.builders.ModelBuilder;
import one.microproject.iamservice.core.model.builders.RoleBuilder;
import one.microproject.iamservice.core.services.impl.persistence.LoggingPersistenceServiceImpl;
import one.microproject.iamservice.core.services.persistence.PersistenceService;

public final class IAMModelBuilders {

    private IAMModelBuilders() {
    }

    public static ModelBuilder modelBuilder(String name, PersistenceService persistenceService) {
        return new ModelBuilder(name, persistenceService);
    }

    public static ModelBuilder modelBuilder(String name) {
        return new ModelBuilder(name, new LoggingPersistenceServiceImpl());
    }

    public static ModelBuilder modelBuilder(ModelId id, String name, PersistenceService persistenceService) {
        return new ModelBuilder(id, name, persistenceService);
    }

    public static ModelBuilder modelBuilder(ModelId id, String name) {
        return new ModelBuilder(id, name, new LoggingPersistenceServiceImpl());
    }

    public static RoleBuilder roleBuilder(String name) {
        return new RoleBuilder(name);
    }

    public static RoleBuilder roleBuilder(RoleId id, String name) {
        return new RoleBuilder(id, name);
    }

}
