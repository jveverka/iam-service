package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.keys.Id;

public class ClientId extends Id {

    @JsonCreator
    public ClientId(@JsonProperty("id") String id) {
        super(id);
    }

    public static ClientId from(String id) {
        return new ClientId(id);
    }

}
