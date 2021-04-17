package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class ClientInfo {

    private final String id;
    private final String name;
    private final Set<String> roles;
    private final Set<String> permissions;

    @JsonCreator
    public ClientInfo(@JsonProperty("id") String id,
                      @JsonProperty("name") String name,
                      @JsonProperty("roles") Set<String> roles,
                      @JsonProperty("permissions") Set<String> permissions) {
        this.id = id;
        this.name = name;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

}
