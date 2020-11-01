package one.microproject.iamservice.server.controller.support;

import one.microproject.iamservice.server.dto.ApiError;
import one.microproject.iamservice.server.services.IAMSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class IAMExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(IAMExceptionHandler.class);

    @ExceptionHandler(IAMSecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleIAMSecurityException(IAMSecurityException e) {
        LOG.info("handleIAMSecurityException {}", e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleExceptions(Exception e) {
        LOG.info("handleExceptions {}", e.getMessage());
        return new ApiError(e.getMessage());
    }

}
