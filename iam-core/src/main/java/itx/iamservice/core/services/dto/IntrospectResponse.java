package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IntrospectResponse {

    private final Boolean active;

    @JsonCreator
    public IntrospectResponse(@JsonProperty("active") Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

}
