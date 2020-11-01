package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.RoleId;

public class CreateRoleRequest {

    private final RoleId id;
    private final String name;

    @JsonCreator
    public CreateRoleRequest(@JsonProperty("id") RoleId id,
                             @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public RoleId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static CreateRoleRequest from(String id, String name) {
        return new CreateRoleRequest(RoleId.from(id), name);
    }

}
