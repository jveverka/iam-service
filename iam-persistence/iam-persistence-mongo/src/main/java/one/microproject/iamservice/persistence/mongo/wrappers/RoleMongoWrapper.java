package one.microproject.iamservice.persistence.mongo.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.keys.ModelKey;

public class RoleMongoWrapper {

    private final String _id;
    private final ModelKey<Role> key;
    private final Role value;

    @JsonCreator
    public RoleMongoWrapper(@JsonProperty("_id") String _id,
                            @JsonProperty("key") ModelKey<Role> key,
                            @JsonProperty("value") Role value) {
        this._id = _id;
        this.key = key;
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public ModelKey<Role> getKey() {
        return key;
    }

    public Role getValue() {
        return value;
    }

}
