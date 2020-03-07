package itx.iamservice.model;

import java.util.Map;

public class Role {

    private final RoleId id;
    private final String name;
    private final Map<String, Permission> permissions;

    public Role(RoleId id, String name, Map<String, Permission> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public RoleId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
