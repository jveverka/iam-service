package one.microproject.iamservice.core.model.builders;

import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.RoleImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class RoleBuilder {

    private RoleId id;
    private final String name;
    private final Collection<Permission> permissions;

    public RoleBuilder(RoleId id, String name) {
        this.id = id;
        this.name = name;
        this.permissions = new ArrayList<>();
    }

    public RoleBuilder(String name) {
        this.name = name;
        this.permissions = new ArrayList<>();
    }

    public RoleBuilder addPermission(String service, String resource, String action) {
        permissions.add(new Permission(service, resource, action));
        return this;
    }

    public RoleBuilder addPermission(Permission permission) {
        permissions.add(permission);
        return this;
    }

    public Role build() {
        if (id == null) {
            id = RoleId.from(UUID.randomUUID().toString());
        }
        return new RoleImpl(id, name, permissions);
    }

    public static RoleBuilder builder(String name) {
        return new RoleBuilder(name);
    }

    public static RoleBuilder builder(RoleId id, String name) {
        return new RoleBuilder(id, name);
    }

}
