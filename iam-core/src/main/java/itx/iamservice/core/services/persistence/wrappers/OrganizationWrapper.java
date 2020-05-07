package itx.iamservice.core.services.persistence.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.keys.ModelKey;

public class OrganizationWrapper {

    private final ModelKey<Organization> key;
    private final Organization value;

    @JsonCreator
    public OrganizationWrapper(@JsonProperty("key") ModelKey<Organization> key,
                               @JsonProperty("value") Organization value) {
        this.key = key;
        this.value = value;
    }

    public ModelKey<Organization> getKey() {
        return key;
    }

    public Organization getValue() {
        return value;
    }

}
