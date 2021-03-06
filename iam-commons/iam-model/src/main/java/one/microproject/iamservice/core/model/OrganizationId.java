package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import one.microproject.iamservice.core.model.keys.Id;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
public final class OrganizationId extends Id {

    @JsonCreator
    public OrganizationId(@JsonProperty("id") String id) {
        super(id);
    }

    public static OrganizationId from(String id) {
        return new OrganizationId(id);
    }

}
