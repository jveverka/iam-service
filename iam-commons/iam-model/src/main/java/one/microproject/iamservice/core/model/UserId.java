package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import one.microproject.iamservice.core.model.keys.Id;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
public final class UserId extends Id {

    @JsonCreator
    public UserId(@JsonProperty("id") String id) {
        super(id);
    }

    public static UserId from(String id) {
        return new UserId(id);
    }

}
