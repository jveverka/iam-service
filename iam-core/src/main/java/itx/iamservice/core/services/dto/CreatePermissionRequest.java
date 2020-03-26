package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePermissionRequest {

    private final String service;
    private final String resource;
    private final String action;

    @JsonCreator
    public CreatePermissionRequest(@JsonProperty("service") String service,
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

}
