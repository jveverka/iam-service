package itx.iamservice.client.impl;

import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.dto.ProviderConfigurationResponse;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.TokenType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public interface IAMServiceProxy extends AutoCloseable {

    boolean waitForInit(long timeout, TimeUnit timeUnit) throws InterruptedException;

    JWKResponse getJWKResponse() throws InterruptedException;

    IntrospectResponse introspect(JWToken token, TokenType typeHint) throws IOException;

    ProviderConfigurationResponse getConfiguration();

}
