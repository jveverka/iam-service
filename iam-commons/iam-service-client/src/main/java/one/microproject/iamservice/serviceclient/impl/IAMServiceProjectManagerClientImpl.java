package one.microproject.iamservice.serviceclient.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.dto.CreateClient;
import one.microproject.iamservice.core.dto.CreateRole;
import one.microproject.iamservice.core.dto.PermissionInfo;
import one.microproject.iamservice.core.dto.RoleInfo;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PermissionId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.dto.ClientInfo;
import one.microproject.iamservice.core.services.dto.ProjectInfo;
import one.microproject.iamservice.core.services.dto.UserInfo;
import one.microproject.iamservice.serviceclient.IAMServiceProjectManagerClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static one.microproject.iamservice.serviceclient.impl.Constants.AUTH_FAILED_ERROR;
import static one.microproject.iamservice.serviceclient.impl.Constants.CLIENTS;
import static one.microproject.iamservice.serviceclient.impl.Constants.DELIMITER;
import static one.microproject.iamservice.serviceclient.impl.Constants.ROLES;
import static one.microproject.iamservice.serviceclient.impl.Constants.SERVICES_DISCOVERY;
import static one.microproject.iamservice.serviceclient.impl.Constants.SERVICES_MANAGEMENT;
import static one.microproject.iamservice.serviceclient.impl.Constants.USERS;

public class IAMServiceProjectManagerClientImpl implements IAMServiceProjectManagerClient {

    private final String accessToken;
    private final URL baseURL;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public IAMServiceProjectManagerClientImpl(String accessToken, URL baseURL, OkHttpClient client, ObjectMapper mapper, OrganizationId organizationId, ProjectId projectId) {
        this.accessToken = accessToken;
        this.baseURL = baseURL;
        this.client = client;
        this.mapper = mapper;
        this.organizationId = organizationId;
        this.projectId = projectId;
    }

    @Override
    public void createRole(CreateRole createRole) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + "/roles")
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .post(RequestBody.create(mapper.writeValueAsString(createRole), MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
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
    public Collection<RoleInfo> getRoles() throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + "/roles")
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), new TypeReference<List<RoleInfo>>(){});
            }
            throw new AuthenticationException(AUTH_FAILED_ERROR + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public Set<PermissionInfo> getPermissions() throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + "/permissions")
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                List<PermissionInfo> permissionInfos = mapper.readValue(response.body().string(), new TypeReference<List<PermissionInfo>>(){});
                return Set.copyOf(permissionInfos);
            }
            throw new AuthenticationException(AUTH_FAILED_ERROR + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void deletePermission(PermissionId permissionId) throws AuthenticationException  {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + "/permissions/" + permissionId.getId())
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
    public void deleteRole(RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + ROLES + roleId.getId())
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
    public ProjectInfo getInfo() throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + SERVICES_DISCOVERY + DELIMITER + organizationId.getId() + DELIMITER + projectId.getId())
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), ProjectInfo.class);
        }
        throw new IOException();
    }

    @Override
    public ClientInfo getClientInfo(ClientId clientId) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + SERVICES_DISCOVERY + DELIMITER + organizationId.getId() + DELIMITER + projectId.getId() + CLIENTS + clientId.getId())
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), ClientInfo.class);
        }
        throw new IOException();
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
        } else {
            throw new IOException();
        }
    }

    @Override
    public void createClient(CreateClient createClient) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + "/clients")
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .post(RequestBody.create(mapper.writeValueAsString(createClient), MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
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
    public void addRoleToClient(ClientId clientId, RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + CLIENTS + clientId.getId() + ROLES + roleId.getId())
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
    public void removeRoleFromClient(ClientId clientId, RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + CLIENTS + clientId.getId() + ROLES + roleId.getId())
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
    public void deleteClient(ClientId clientId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + CLIENTS + clientId.getId())
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
    public void setAudience(Set<String> audience) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + SERVICES_MANAGEMENT + organizationId.getId() + DELIMITER + projectId.getId() + "/audience")
                    .addHeader(IAMServiceManagerClientImpl.AUTHORIZATION, IAMServiceManagerClientImpl.BEARER_PREFIX + accessToken)
                    .put(RequestBody.create(mapper.writeValueAsString(audience), MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
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
