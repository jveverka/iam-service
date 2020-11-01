package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class CreateRole {

    private final String id;
    private final String name;
    private final Set<PermissionInfo> permissions;

    @JsonCreator
    public CreateRole(@JsonProperty("id") String id,
                      @JsonProperty("name") String name,
                      @JsonProperty("permissions") Set<PermissionInfo> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<PermissionInfo> getPermissions() {
        return permissions;
    }
}
