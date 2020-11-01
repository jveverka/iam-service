package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.keys.Id;

public final class ProjectId extends Id {

    @JsonCreator
    public ProjectId(@JsonProperty("id") String id) {
        super(id);
    }

    public static ProjectId from(String id) {
        return new ProjectId(id);
    }

}
