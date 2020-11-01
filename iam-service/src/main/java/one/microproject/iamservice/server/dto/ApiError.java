package one.microproject.iamservice.server.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiError {

    private final String message;

    @JsonCreator
    public ApiError(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
