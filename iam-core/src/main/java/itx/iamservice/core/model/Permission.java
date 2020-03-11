package itx.iamservice.core.model;

import java.util.Objects;

public class Permission {

    private final PermissionId id;
    private final String service;
    private final String resource;
    private final String action;

    public Permission(String service, String resource, String action) {
        this.id = PermissionId.from(service + "." + resource + "." + action);
        this.service = service;
        this.resource = resource;
        this.action = action;
    }

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

}
