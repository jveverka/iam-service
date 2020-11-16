package one.microproject.iamservice.persistence.mongo.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.keys.ModelKey;

public class UserMongoWrapper {

    private final String _id;
    private final ModelKey<User> key;
    private final User value;

    @JsonCreator
    public UserMongoWrapper(@JsonProperty("_id") String _id,
                            @JsonProperty("key") ModelKey<User> key,
                            @JsonProperty("value") User value) {
        this._id = _id;
        this.key = key;
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public ModelKey<User> getKey() {
        return key;
    }

    public User getValue() {
        return value;
    }

}
