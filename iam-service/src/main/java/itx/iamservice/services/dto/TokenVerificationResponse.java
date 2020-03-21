package itx.iamservice.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenVerificationResponse {

    private final boolean valid;

    @JsonCreator
    public TokenVerificationResponse(@JsonProperty("valid") boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

}
