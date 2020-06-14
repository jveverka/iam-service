package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.OrganizationId;

public class CreateOrganizationRequest {

    private final OrganizationId id;
    private final String name;

    @JsonCreator
    public CreateOrganizationRequest(@JsonProperty("id") OrganizationId id,
                                     @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public OrganizationId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static CreateOrganizationRequest from(String id, String name) {
        return new CreateOrganizationRequest(OrganizationId.from(id), name);
    }

}
