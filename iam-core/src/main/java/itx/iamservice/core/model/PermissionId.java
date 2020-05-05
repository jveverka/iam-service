package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.keys.Id;

public class PermissionId extends Id {

    @JsonCreator
    public PermissionId(@JsonProperty("id") String id) {
        super(id);
    }

    public static PermissionId from(String id) {
        return new PermissionId(id);
    }

}
