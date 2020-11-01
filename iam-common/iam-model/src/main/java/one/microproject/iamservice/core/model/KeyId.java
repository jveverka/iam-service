package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.keys.Id;

public class KeyId extends Id {

    @JsonCreator
    public KeyId(@JsonProperty("id") String id) {
        super(id);
    }

    public static KeyId from(String id) {
        return new KeyId(id);
    }

}
