package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class RoleInfo extends CreateRole {

    @JsonCreator
    public RoleInfo(@JsonProperty("id") String id,
                    @JsonProperty("name") String name,
                    @JsonProperty("permissions") Set<PermissionInfo> permissions) {
        super(id, name, permissions);
    }

}
