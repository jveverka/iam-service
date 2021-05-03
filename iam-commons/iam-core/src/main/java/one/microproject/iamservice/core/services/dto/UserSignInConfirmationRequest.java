package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSignInConfirmationRequest {

    private final String code;

    @JsonCreator
    public UserSignInConfirmationRequest(@JsonProperty("code") String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
