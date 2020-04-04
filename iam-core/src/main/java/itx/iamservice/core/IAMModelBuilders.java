package itx.iamservice.core;

import itx.iamservice.core.model.builders.ModelBuilder;
import itx.iamservice.core.model.builders.RoleBuilder;

public final class IAMModelBuilders {

    private IAMModelBuilders() {
    }

    public static ModelBuilder modelBuilder(String name) {
        return new ModelBuilder(name);
    }

    public static RoleBuilder roleBuilder(String name) {
        return new RoleBuilder(name);
    }

}
