package one.microproject.iamservice.core.dto;

public enum ErrorType {

    //https://tools.ietf.org/html/rfc6749#section-4.1.2.1
    //https://tools.ietf.org/html/rfc6749#section-4.2.2.1
    INVALID_REQUEST("invalid_request"),
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    ACCESS_DENIED("access_denied"),
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),
    INVALID_SCOPE("invalid_scope"),
    SERVER_ERROR("server_error"),
    TEMPORARILY_UNAVAILABLE("temporarily_unavailable"),

    //https://tools.ietf.org/html/rfc6749#section-5.2
    INVALID_CLIENT("invalid_client"),
    INVALID_GRANT("invalid_grant"),
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type");

    private final String error;

    ErrorType(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

}
