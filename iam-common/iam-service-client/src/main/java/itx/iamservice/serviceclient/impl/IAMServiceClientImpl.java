package itx.iamservice.serviceclient.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.serviceclient.IAMServiceClient;
import itx.iamservice.serviceclient.IAMServiceProject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class IAMServiceClientImpl implements IAMServiceClient {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String APPLICATION_JSON = "application/json";

    private final String baseURL;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public IAMServiceClientImpl(String baseURL, Long timeoutDuration,  TimeUnit timeUnit) {
        this.baseURL = baseURL;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(timeoutDuration, timeUnit)
                .build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public TokenResponse getAccessTokens(OrganizationId organizationId, ProjectId projectId, String userName, String password, ClientId clientId, String clientSecret) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/token" +
                            "?grant_type=password" +
                            "&username=" + userName +
                            "&scope=&password=" + password +
                            "&client_id=" + clientId.getId() +
                            "&client_secret=" + clientSecret)
                    .post(RequestBody.create("{}", MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), TokenResponse.class);
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public TokenResponse getAccessTokensForIAMAdmin(String password, String clientSecret) throws AuthenticationException {
        return getAccessTokens(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, ModelUtils.IAM_ADMIN_USER.getId(), password, ModelUtils.IAM_ADMIN_CLIENT_ID, clientSecret);
    }

    @Override
    public SetupOrganizationResponse setupOrganization(String accessToken, SetupOrganizationRequest setupOrganizationRequest) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/admin/organization/setup")
                    .addHeader(AUTHORIZATION, BEARER_PREFIX + accessToken)
                    .post(RequestBody.create(mapper.writeValueAsString(setupOrganizationRequest), MediaType.parse(APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), SetupOrganizationResponse.class);
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void deleteOrganizationRecursively(String accessToken, OrganizationId organizationId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/admin/organization/" + organizationId.getId())
                    .addHeader(AUTHORIZATION, BEARER_PREFIX + accessToken)
                    .delete()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return;
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public IAMServiceProject getIAMServiceProject(String accessToken, OrganizationId organizationId, ProjectId projectId) {
        return new IAMServiceProjectImpl(accessToken, baseURL, client, mapper, organizationId, projectId);
    }

}
