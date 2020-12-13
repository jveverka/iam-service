package one.microproject.iamservice.serviceclient.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.dto.ProviderConfigurationResponse;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.services.dto.UserInfoResponse;
import one.microproject.iamservice.serviceclient.IAMServiceStatusClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

public class IAMServiceStatusClientImpl implements IAMServiceStatusClient {

    private final URL baseURL;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public IAMServiceStatusClientImpl(URL baseURL, OkHttpClient client, ObjectMapper mapper, OrganizationId organizationId, ProjectId projectId) {
        this.baseURL = baseURL;
        this.client = client;
        this.mapper = mapper;
        this.organizationId = organizationId;
        this.projectId = projectId;
    }

    @Override
    public ProviderConfigurationResponse getProviderConfiguration() throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/.well-known/openid-configuration")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), ProviderConfigurationResponse.class);
        }
        throw new IOException();
    }

    @Override
    public JWKResponse getJWK() throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/.well-known/jwks.json")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), JWKResponse.class);
        }
        throw new IOException();
    }

    @Override
    public UserInfoResponse getUserInfo(String accessToken) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/userinfo")
                .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), UserInfoResponse.class);
        }
        throw new IOException();
    }

    @Override
    public void revokeToken(String accessToken, String tokenTypeHint) throws IOException {
        String url = "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/revoke?token=" + accessToken;
        if (tokenTypeHint != null) {
            url = url + "&token_type_hint=" + tokenTypeHint;
        }
        Request request = new Request.Builder()
                .url(baseURL + url)
                .post(RequestBody.create("", MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return;
        }
        throw new IOException();
    }

    @Override
    public void revokeToken(String accessToken) throws IOException {
        revokeToken(accessToken, null);
    }

    @Override
    public IntrospectResponse tokenIntrospection(String accessToken, String tokenTypeHint) throws IOException {
        String url = "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/introspect?token=" + accessToken;
        if (tokenTypeHint != null) {
            url = url + "&token_type_hint=" + tokenTypeHint;
        }
        Request request = new Request.Builder()
                .url(baseURL + url)
                .post(RequestBody.create("", MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), IntrospectResponse.class);
        }
        throw new IOException();
    }

    @Override
    public IntrospectResponse tokenIntrospection(String accessToken) throws IOException {
        return tokenIntrospection(accessToken, null);
    }

    @Override
    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

}
