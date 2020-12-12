package one.microproject.iamservice.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.dto.IntrospectRequest;
import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.dto.ProviderConfigurationResponse;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.TokenType;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IAMServiceHttpProxyImpl implements IAMServiceProxy {

    private static final Logger LOG = LoggerFactory.getLogger(IAMServiceHttpProxyImpl.class);

    private static final long INITIAL_DELAY = 1;
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final URL baseUrl;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final ScheduledExecutorService executor;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final CountDownLatch cl;

    private JWKResponse jwkResponse;
    private ProviderConfigurationResponse providerConfigurationResponse;

    public IAMServiceHttpProxyImpl(URL baseUrl, OrganizationId organizationId, ProjectId projectId,
                                   Long pollingInterval, TimeUnit timeUnit) {
        this.baseUrl = baseUrl;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.executor = Executors.newScheduledThreadPool(1);
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
        this.cl = new CountDownLatch(1);
        this.executor.scheduleWithFixedDelay(new IAMServiceHttpFetchTask(baseUrl, organizationId, projectId, client, mapper, this), INITIAL_DELAY, pollingInterval, timeUnit);
    }

    @Override
    public boolean waitForInit(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return cl.await(timeout, timeUnit);
    }

    @Override
    public JWKResponse getJWKResponse() {
        LOG.error("jwkResponse == NULL ? {}", (jwkResponse == null));
        return jwkResponse;
    }

    @Override
    public IntrospectResponse introspect(JWToken token, TokenType typeHint) throws IOException {
        IntrospectRequest introspectRequest = new IntrospectRequest(token, typeHint);
        String postBody = mapper.writeValueAsString(introspectRequest);
        Request request = new Request.Builder()
                .url(baseUrl.toString() + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/introspect")
                .post(RequestBody.create(postBody, MediaType.parse(APPLICATION_JSON)))
                .build();
        Response response = client.newCall(request).execute();
        return mapper.readValue(response.body().string(), IntrospectResponse.class);
    }

    @Override
    public ProviderConfigurationResponse getConfiguration() {
        if (providerConfigurationResponse == null) {
            Request request = new Request.Builder()
                    .url(baseUrl.toString() + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/.well-known/openid-configuration")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    providerConfigurationResponse = mapper.readValue(response.body().string(), ProviderConfigurationResponse.class);
                } else {
                    LOG.warn("HTTP response failed");
                }
            } catch (Exception e) {
                LOG.error("Error: ", e);
            }
        }
        return providerConfigurationResponse;
    }

    @Override
    public void updateKeyCache() {
        IAMServiceHttpFetchTask task = new IAMServiceHttpFetchTask(baseUrl, organizationId, projectId, client, mapper, this);
        task.run();
    }

    @Override
    public Optional<TokenResponse> getTokens(Code code, String state) {
        /*
        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/token" +
                            "?grant_type=authorization_code" +
                            "&code=" + code.getCodeValue() + "&state=" + state)
                    .post(RequestBody.create("{}", MediaType.parse(APPLICATION_FORM_URLENCODED)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return Optional.of(mapper.readValue(response.body().string(), TokenResponse.class));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        */
        return getTokens(code, state, "");
    }

    @Override
    public Optional<TokenResponse> getTokens(Code code, String state, String codeVerifier) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if (!codeVerifier.isEmpty()) {
                builder.add("code_verifier", codeVerifier);
            }
            Request request = new Request.Builder()
                    .header("Content-Type", APPLICATION_FORM_URLENCODED)
                    .url(baseUrl + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/token" +
                            "?grant_type=authorization_code" +
                            "&code=" + code.getCodeValue() + "&state=" + state)
                    .post(builder.build())
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return Optional.of(mapper.readValue(response.body().string(), TokenResponse.class));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    protected synchronized void setJwkResponse(JWKResponse jwkResponse) {
        LOG.debug("JWK cache updated");
        this.jwkResponse = jwkResponse;
        this.cl.countDown();
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

}
