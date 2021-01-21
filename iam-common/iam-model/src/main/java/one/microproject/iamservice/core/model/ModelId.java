package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import one.microproject.iamservice.core.model.keys.Id;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
public class ModelId extends Id {

    @JsonCreator
    public ModelId(@JsonProperty("id") String id) {
        super(id);
    }

    public static ModelId from(String id) {
        return new ModelId(id);
    }

}
