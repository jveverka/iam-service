package one.microproject.iamservice.serviceclient.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.dto.CreateUser;
import one.microproject.iamservice.core.dto.UserCredentialsChangeRequest;
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

import static one.microproject.iamservice.serviceclient.impl.Constants.AUTH_FAILED_ERROR;
import static one.microproject.iamservice.serviceclient.impl.Constants.DELIMITER;
import static one.microproject.iamservice.serviceclient.impl.Constants.SERVICES_DISCOVERY;
import static one.microproject.iamservice.serviceclient.impl.Constants.SERVICES_MANAGEMENT;
import static one.microproject.iamservice.serviceclient.impl.Constants.USERS;

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
                .url(baseURL + SERVICES_DISCOVERY + DELIMITER + organizationId.getId() + DELIMITER + projectId.getId() + USERS + userId.getId())
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
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + USERS)
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .post(RequestBody.create(mapper.writeValueAsString(createUser), MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return;
            }
            throw new AuthenticationException(AUTH_FAILED_ERROR + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void deleteUser(UserId userId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + USERS + userId.getId())
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .delete()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return;
            }
            throw new AuthenticationException(AUTH_FAILED_ERROR + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void addRoleToUser(UserId userId, RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + USERS + userId.getId() + "/roles/" + roleId.getId())
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .put(RequestBody.create("{}", MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return;
            }
            throw new AuthenticationException(AUTH_FAILED_ERROR + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void removeRoleFromUser(UserId userId, RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + USERS + userId.getId() + "/roles/" + roleId.getId())
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .delete()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return;
            }
            throw new AuthenticationException(AUTH_FAILED_ERROR + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void changeUserCredentials(UserId userId, String userAccessToken, UserCredentialsChangeRequest userCredentialsChangeRequest) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + USERS + userId.getId() + "/change-password")
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + userAccessToken)
                    .put(RequestBody.create(mapper.writeValueAsString(userCredentialsChangeRequest), MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return;
            }
            throw new AuthenticationException(AUTH_FAILED_ERROR + response.code());
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
