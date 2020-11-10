package one.microproject.iamservice.core;

import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.builders.ModelBuilder;
import one.microproject.iamservice.core.model.builders.RoleBuilder;
import one.microproject.iamservice.core.services.impl.persistence.LoggingPersistenceServiceImpl;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;

public final class IAMModelBuilders {

    private IAMModelBuilders() {
    }

    public static ModelBuilder modelBuilder(ModelWrapper modelWrapper) {
        return new ModelBuilder(modelWrapper);
    }

    public static RoleBuilder roleBuilder(String name) {
        return new RoleBuilder(name);
    }

    public static RoleBuilder roleBuilder(RoleId id, String name) {
        return new RoleBuilder(id, name);
    }

}
