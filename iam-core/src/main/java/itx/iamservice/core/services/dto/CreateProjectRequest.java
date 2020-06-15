package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.ProjectId;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CreateProjectRequest {

    private final ProjectId id;
    private final String name;
    private final Set<String> audience;

    @JsonCreator
    public CreateProjectRequest(@JsonProperty("id") ProjectId id,
                                @JsonProperty("name") String name,
                                @JsonProperty("audience") Collection<String> audience) {
        this.id = id;
        this.name = name;
        this.audience = new HashSet<>();
        audience.forEach(a->this.audience.add(a));
    }

    public ProjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAudience() {
        return audience;
    }

    public static CreateProjectRequest from(String id, String name, String ... audience) {

        return new CreateProjectRequest(ProjectId.from(id), name, Arrays.asList(audience));
    }

    public static CreateProjectRequest from(ProjectId id, String name, String ... audience) {
        return new CreateProjectRequest(id, name, Arrays.asList(audience));
    }

}
