package itx.iamservice.server.services;

public interface IAMSecurityValidator {

    /**
     * Validate Admin's authentication.
     * @param authorization - http 'Authentication' header. Expected format: "Bearer [JWT]".
     * @throws IAMSecurityException
     */
    void validate(String authorization) throws IAMSecurityException;

}
