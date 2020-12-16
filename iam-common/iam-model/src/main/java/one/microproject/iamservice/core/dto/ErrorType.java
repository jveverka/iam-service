package one.microproject.iamservice.core.dto;

public enum ErrorType {

    //https://tools.ietf.org/html/rfc6749#section-4.1.2.1
    //https://tools.ietf.org/html/rfc6749#section-4.2.2.1
    invalid_request,
    unauthorized_client,
    access_denied,
    unsupported_response_type,
    invalid_scope,
    server_error,
    temporarily_unavailable,

    //https://tools.ietf.org/html/rfc6749#section-5.2
    invalid_client,
    invalid_grant,
    unsupported_grant_type

}
