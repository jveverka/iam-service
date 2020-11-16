package one.microproject.iamservice.persistence.mongo.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.keys.ModelKey;

public class ProjectMongoWrapper {

    private final String _id;
    private final ModelKey<Project> key;
    private final Project value;

    @JsonCreator
    public ProjectMongoWrapper(@JsonProperty("_id") String _id,
                               @JsonProperty("key") ModelKey<Project> key,
                               @JsonProperty("value") Project value) {
        this._id = _id;
        this.key = key;
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public ModelKey<Project> getKey() {
        return key;
    }

    public Project getValue() {
        return value;
    }

}
