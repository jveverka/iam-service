package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Permission {

    private final PermissionId id;
    private final String service;
    private final String resource;
    private final String action;

    @JsonCreator
    public Permission(@JsonProperty("service") String service,
                      @JsonProperty("resource") String resource,
                      @JsonProperty("action") String action) {
        this.id = PermissionId.from(service + "." + resource + "." + action);
        this.service = service;
        this.resource = resource;
        this.action = action;
    }

    @JsonIgnore
    public PermissionId getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String asStringValue() {
        return id.getId();
    }

    public static Permission from(String permission) throws PermissionParsingException {
        try {
            String[] splits = permission.split("\\.");
            return new Permission(splits[0], splits[1], splits[2]);
        } catch(Exception e) {
            throw new PermissionParsingException(e);
        }
    }

    public static Permission from(PermissionId permissionId) throws PermissionParsingException {
        try {
            String[] splits = permissionId.getId().split("\\.");
            return new Permission(splits[0], splits[1], splits[2]);
        } catch(Exception e) {
            throw new PermissionParsingException(e);
        }
    }

}
