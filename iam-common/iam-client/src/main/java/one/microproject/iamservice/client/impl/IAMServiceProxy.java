package one.microproject.iamservice.client.impl;

import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.dto.ProviderConfigurationResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.TokenType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public interface IAMServiceProxy extends AutoCloseable {

    boolean waitForInit(long timeout, TimeUnit timeUnit) throws InterruptedException;

    JWKResponse getJWKResponse() throws InterruptedException;

    IntrospectResponse introspect(JWToken token, TokenType typeHint) throws IOException;

    ProviderConfigurationResponse getConfiguration();

    boolean updateKeyCache();

    TokenResponseWrapper getTokens(Code code, String state) throws IOException;

    TokenResponseWrapper getTokens(Code code, String state, String codeVerifier) throws IOException;

}
