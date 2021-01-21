package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import one.microproject.iamservice.core.model.keys.Id;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
public final class RoleId extends Id {

    @JsonCreator
    public RoleId(@JsonProperty("id") String id) {
        super(id);
    }

    public static RoleId from(String id) {
        return new RoleId(id);
    }

}
