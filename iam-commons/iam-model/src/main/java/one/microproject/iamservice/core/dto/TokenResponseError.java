package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponseError {

    @JsonProperty("error")
    private final String error;

    @JsonProperty("error_description")
    private final String errorDescription;

    @JsonProperty("state")
    private final String state;

    @JsonCreator
    public TokenResponseError(@JsonProperty("error") String error,
                              @JsonProperty("error_description") String errorDescription,
                              @JsonProperty("state") String state) {
        this.error = error;
        this.errorDescription = errorDescription;
        this.state = state;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    @JsonProperty("error_description")
    public String getErrorDescription() {
        return errorDescription;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    public static TokenResponseError from(ErrorType error, String errorDescription) {
        return new TokenResponseError(error.getError(), errorDescription, null);
    }

    public static TokenResponseError from(ErrorType error, String errorDescription, String state) {
        return new TokenResponseError(error.getError(), errorDescription, state);
    }

}
