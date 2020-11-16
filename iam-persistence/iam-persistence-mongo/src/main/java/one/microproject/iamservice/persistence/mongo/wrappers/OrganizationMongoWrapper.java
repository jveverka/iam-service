package one.microproject.iamservice.persistence.mongo.wrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.keys.ModelKey;

public class OrganizationMongoWrapper {

    private final String _id;
    private final ModelKey<Organization> key;
    private final Organization value;

    @JsonCreator
    public OrganizationMongoWrapper(@JsonProperty("_id") String _id,
                                    @JsonProperty("key") ModelKey<Organization> key,
                                    @JsonProperty("value") Organization value) {
        this._id = _id;
        this.key  = key;
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public ModelKey<Organization> getKey() {
        return key;
    }

    public Organization getValue() {
        return value;
    }

}
