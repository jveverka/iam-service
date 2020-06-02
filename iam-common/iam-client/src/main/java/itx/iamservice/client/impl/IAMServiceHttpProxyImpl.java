package itx.iamservice.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.dto.IntrospectRequest;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.dto.ProviderConfigurationResponse;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.TokenType;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IAMServiceHttpProxyImpl implements IAMServiceProxy {

    private static final Logger LOG = LoggerFactory.getLogger(IAMServiceHttpProxyImpl.class);

    private final URL baseUrl;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final ScheduledExecutorService executor;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

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
        this.executor.scheduleWithFixedDelay(new IAMServiceHttpFetchTask(baseUrl, organizationId, projectId, client, mapper, this), 1, pollingInterval, timeUnit);
    }

    @Override
    public JWKResponse getJWKResponse() {
        return jwkResponse;
    }

    @Override
    public IntrospectResponse introspect(JWToken token, TokenType typeHint) throws IOException {
        IntrospectRequest introspectRequest = new IntrospectRequest(token, typeHint);
        String postBody = mapper.writeValueAsString(introspectRequest);
        Request request = new Request.Builder()
                .url(baseUrl.toString() + "/" + organizationId.getId() + "/" + projectId.getId() + "/introspect")
                .post(RequestBody.create(postBody, MediaType.parse("application/json")))
                .build();
        Response response = client.newCall(request).execute();
        return mapper.readValue(response.body().string(), IntrospectResponse.class);
    }

    @Override
    public ProviderConfigurationResponse getConfiguration() {
        if (providerConfigurationResponse == null) {
            Request request = new Request.Builder()
                    .url(baseUrl.toString() + "/" + organizationId.getId() + "/" + projectId.getId() + "/.well-known/openid-configuration")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    providerConfigurationResponse = mapper.readValue(response.body().string(), ProviderConfigurationResponse.class);
                } else {
                    LOG.info("HTTP response failed");
                }
            } catch (Exception e) {
                LOG.error("Error: ", e);
            }
        }
        return providerConfigurationResponse;
    }

    protected void setJwkResponse(JWKResponse jwkResponse) {
        this.jwkResponse = jwkResponse;
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

}
