package one.microproject.iamservice.serviceclient.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.dto.CreateUser;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.dto.UserInfo;
import one.microproject.iamservice.serviceclient.IAMServiceUserManagerClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

public class IAMServiceUserManagerClientImpl implements IAMServiceUserManagerClient {

    private final String accessToken;
    private final URL baseURL;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public IAMServiceUserManagerClientImpl(String accessToken, URL baseURL, OkHttpClient client, ObjectMapper mapper, OrganizationId organizationId, ProjectId projectId) {
        this.accessToken = accessToken;
        this.baseURL = baseURL;
        this.client = client;
        this.mapper = mapper;
        this.organizationId = organizationId;
        this.projectId = projectId;
    }

    @Override
    public UserInfo getUserInfo(UserId userId) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId() + "/users/" + userId.getId())
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), UserInfo.class);
        }
        throw new IOException();
    }

    @Override
    public void createUser(CreateUser createUser) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/users")
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .post(RequestBody.create(mapper.writeValueAsString(createUser), MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
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
    public void deleteUser(UserId userId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/users/" + userId.getId())
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
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
    public void addRoleToUser(UserId userId, RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/users/" + userId.getId() + "/roles/" + roleId.getId())
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .put(RequestBody.create("{}", MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
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
    public void removeRoleFromUser(UserId userId, RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/users/" + userId.getId() + "/roles/" + roleId.getId())
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
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
    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

}
