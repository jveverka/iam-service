package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateProjectRequest {

    private final String name;

    @JsonCreator
    public CreateProjectRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CreateProjectRequest from(String name) {
        return new CreateProjectRequest(name);
    }

}
