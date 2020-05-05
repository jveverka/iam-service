package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.keys.Id;

public class KeyPairId extends Id {

    @JsonCreator
    public KeyPairId(@JsonProperty("id") String id) {
        super(id);
    }

    public static KeyPairId from(String id) {
        return new KeyPairId(id);
    }

}
