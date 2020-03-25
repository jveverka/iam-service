package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelId extends Id {

    @JsonCreator
    public ModelId(@JsonProperty("id") String id) {
        super(id);
    }

    public static ModelId from(String id) {
        return new ModelId(id);
    }

}
