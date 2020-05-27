package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.keys.Id;

public final class RoleId extends Id {

    @JsonCreator
    public RoleId(@JsonProperty("id") String id) {
        super(id);
    }

    public static RoleId from(String id) {
        return new RoleId(id);
    }

}
