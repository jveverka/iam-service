package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleImpl implements Role {

    private final RoleId id;
    private final String name;
    private final Set<Permission> permissions;

    public RoleImpl(RoleId id, String name) {
        this.id = id;
        this.name = name;
        this.permissions = new HashSet<>();
    }

    @JsonCreator
    public RoleImpl(@JsonProperty("id") RoleId id,
                @JsonProperty("name") String name,
                @JsonProperty("permissions") Collection<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = new HashSet<>();
        permissions.forEach(this.permissions::add);
    }

    @Override
    public RoleId getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    @Override
    public Collection<Permission> getPermissions() {
        return permissions.stream().collect(Collectors.toList());
    }

    @Override
    public boolean removePermission(PermissionId id) {
        Set<Permission> filtered = permissions.stream().filter(p->!p.getId().equals(id)).collect(Collectors.toSet());
        if (filtered.size() < permissions.size()) {
            permissions.clear();
            permissions.addAll(filtered);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleImpl role = (RoleImpl) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
