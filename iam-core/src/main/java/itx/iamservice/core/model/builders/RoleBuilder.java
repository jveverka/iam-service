package itx.iamservice.core.model.builders;

import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class RoleBuilder {

    private final String name;
    private final Collection<Permission> permissions;

    public RoleBuilder(String name) {
        this.name = name;
        this.permissions = new ArrayList<>();
    }

    public RoleBuilder addPermission(String service, String resource, String action) {
        permissions.add(new Permission(service, resource, action));
        return this;
    }

    public Role build() {
        RoleId id = RoleId.from(UUID.randomUUID().toString());
        return new Role(id, name, permissions);
    }

    public static RoleBuilder builder(String name) {
        return new RoleBuilder(name);
    }

}
