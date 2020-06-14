package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.ProjectId;

public class CreateProjectRequest {

    private final ProjectId id;
    private final String name;

    @JsonCreator
    public CreateProjectRequest(@JsonProperty("id") ProjectId id,
                                @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public ProjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static CreateProjectRequest from(String id, String name) {
        return new CreateProjectRequest(ProjectId.from(id), name);
    }

    public static CreateProjectRequest from(ProjectId id, String name) {
        return new CreateProjectRequest(id, name);
    }

}
