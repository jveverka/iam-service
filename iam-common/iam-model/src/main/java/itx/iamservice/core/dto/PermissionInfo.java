package itx.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class PermissionInfo {

    private final String service;
    private final String resource;
    private final String action;

    @JsonCreator
    public PermissionInfo(@JsonProperty("service") String service,
                          @JsonProperty("resource") String resource,
                          @JsonProperty("action") String action) {
        this.service = service;
        this.resource = resource;
        this.action = action;
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
        PermissionInfo that = (PermissionInfo) o;
        return Objects.equals(service, that.service) &&
                Objects.equals(resource, that.resource) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, resource, action);
    }

    public static PermissionInfo from(String service, String resource, String action) {
        return new PermissionInfo(service, resource, action);
    }

}
