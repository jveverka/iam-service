package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrganizationRequest {

    private final String name;

    @JsonCreator
    public CreateOrganizationRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CreateOrganizationRequest from(String name) {
        return new CreateOrganizationRequest(name);
    }

}
