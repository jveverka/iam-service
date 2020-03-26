package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateRoleRequest {

    private final String name;

    @JsonCreator
    public CreateRoleRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CreateRoleRequest from(String name) {
        return new CreateRoleRequest(name);
    }

}
