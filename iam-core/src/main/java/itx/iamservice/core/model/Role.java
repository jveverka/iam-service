package itx.iamservice.core.model;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Role {

    private final RoleId id;
    private final String name;
    private final Map<PermissionId, Permission> permissions;

    public Role(RoleId id, String name) {
        this.id = id;
        this.name = name;
        this.permissions = new ConcurrentHashMap<>();
    }

    public RoleId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addPermission(Permission permission) {
        permissions.put(permission.getId(), permission);
    }

    public Optional<Permission> getPermission(PermissionId id) {
        return Optional.ofNullable(permissions.get(id));
    }

    public Collection<Permission> getPermissions() {
        return permissions.values().stream().collect(Collectors.toList());
    }

    public boolean removePermission(PermissionId id) {
        return permissions.remove(id) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
