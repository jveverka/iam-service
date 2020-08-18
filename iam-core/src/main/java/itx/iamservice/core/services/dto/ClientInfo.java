package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.RoleId;

import java.util.Set;

public class ClientInfo {

    private final ClientId id;
    private final String name;
    private final Set<RoleId> roles;
    private final Set<Permission> permissions;

    @JsonCreator
    public ClientInfo(@JsonProperty("id") ClientId id,
                      @JsonProperty("name") String name,
                      @JsonProperty("roles") Set<RoleId> roles,
                      @JsonProperty("permissions") Set<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.roles = roles;
        this.permissions = permissions;
    }

    public ClientId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<RoleId> getRoles() {
        return roles;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

}
