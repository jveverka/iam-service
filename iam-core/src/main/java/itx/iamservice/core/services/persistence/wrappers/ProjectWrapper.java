package itx.iamservice.core.services.persistence.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.keys.ModelKey;

public class ProjectWrapper {

    private final ModelKey<Project> key;
    private final Project value;

    @JsonCreator
    public ProjectWrapper(@JsonProperty("key") ModelKey<Project> key,
                          @JsonProperty("value") Project value) {
        this.key = key;
        this.value = value;
    }

    public ModelKey<Project> getKey() {
        return key;
    }

    public Project getValue() {
        return value;
    }

}
