package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import one.microproject.iamservice.core.model.keys.Id;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
public final class ProjectId extends Id {

    @JsonCreator
    public ProjectId(@JsonProperty("id") String id) {
        super(id);
    }

    public static ProjectId from(String id) {
        return new ProjectId(id);
    }

}
