package itx.iamservice.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRevokeResponse {

    private final boolean valid;

    @JsonCreator
    public TokenRevokeResponse(@JsonProperty("valid") boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

}
