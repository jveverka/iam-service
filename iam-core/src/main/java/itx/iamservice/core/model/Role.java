package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Role {

    private final RoleId id;
    private final String name;
    private final Set<Permission> permissions;

    public Role(RoleId id, String name) {
        this.id = id;
        this.name = name;
        this.permissions = new HashSet<>();
    }

    @JsonCreator
    public Role(@JsonProperty("id") RoleId id,
                @JsonProperty("name") String name,
                @JsonProperty("permissions") Collection<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = new HashSet<>();
        permissions.forEach(permission ->
            this.permissions.add(permission)
        );
    }

    public RoleId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public Collection<Permission> getPermissions() {
        return permissions.stream().collect(Collectors.toList());
    }

    public boolean removePermission(PermissionId id) {
        return permissions.remove(id);
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
