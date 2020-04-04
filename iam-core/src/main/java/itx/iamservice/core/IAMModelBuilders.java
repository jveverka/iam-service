package itx.iamservice.core;

import itx.iamservice.core.model.ModelId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.builders.ModelBuilder;
import itx.iamservice.core.model.builders.RoleBuilder;

public final class IAMModelBuilders {

    private IAMModelBuilders() {
    }

    public static ModelBuilder modelBuilder(String name) {
        return new ModelBuilder(name);
    }

    public static ModelBuilder modelBuilder(ModelId id, String name) {
        return new ModelBuilder(id, name);
    }

    public static RoleBuilder roleBuilder(String name) {
        return new RoleBuilder(name);
    }

    public static RoleBuilder roleBuilder(RoleId id, String name) {
        return new RoleBuilder(id, name);
    }

}
