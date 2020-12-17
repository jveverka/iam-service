package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredentialsChangeRequest {

    private final String newPassword;

    @JsonCreator
    public UserCredentialsChangeRequest(@JsonProperty("newPassword") String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

}
