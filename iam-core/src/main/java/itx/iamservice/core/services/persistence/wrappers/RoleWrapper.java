package itx.iamservice.core.services.persistence.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.keys.ModelKey;

public class RoleWrapper {

    private final ModelKey<Role> key;
    private final Role value;

    @JsonCreator
    public RoleWrapper(@JsonProperty("key") ModelKey<Role> key,
                       @JsonProperty("value") Role value) {
        this.key = key;
        this.value = value;
    }

    public ModelKey<Role> getKey() {
        return key;
    }

    public Role getValue() {
        return value;
    }

}
