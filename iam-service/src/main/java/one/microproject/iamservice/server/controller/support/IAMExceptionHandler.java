package one.microproject.iamservice.server.controller.support;

import one.microproject.iamservice.core.dto.TokenResponseError;
import one.microproject.iamservice.server.dto.ApiError;
import one.microproject.iamservice.server.services.IAMSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class IAMExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(IAMExceptionHandler.class);

    @ExceptionHandler(IAMSecurityException.class)
    public ResponseEntity<ApiError> handleIAMSecurityException(IAMSecurityException e) {
        LOG.info("handleIAMSecurityException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(OAuth2TokenException.class)
    public ResponseEntity<TokenResponseError> handleOAuth2TokenException(OAuth2TokenException e) {
        LOG.info("handleOAuth2TokenException: {}", e.getTokenResponseError().getError());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getTokenResponseError());
    }

}
