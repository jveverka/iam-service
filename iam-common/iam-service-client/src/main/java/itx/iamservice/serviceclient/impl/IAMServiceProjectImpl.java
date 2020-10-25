package itx.iamservice.serviceclient.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.dto.CreateClient;
import itx.iamservice.core.dto.CreateRole;
import itx.iamservice.core.dto.PermissionInfo;
import itx.iamservice.core.dto.RoleInfo;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.ClientInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.serviceclient.IAMServiceProject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static itx.iamservice.serviceclient.impl.IAMServiceClientImpl.APPLICATION_JSON;
import static itx.iamservice.serviceclient.impl.IAMServiceClientImpl.AUTHORIZATION;
import static itx.iamservice.serviceclient.impl.IAMServiceClientImpl.BEARER_PREFIX;

public class IAMServiceProjectImpl implements IAMServiceProject {

    private final String accessToken;
    private final String baseURL;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public IAMServiceProjectImpl(String accessToken, String baseURL, OkHttpClient client, ObjectMapper mapper, OrganizationId organizationId, ProjectId projectId) {
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
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/roles")
                    .addHeader(AUTHORIZATION, BEARER_PREFIX + accessToken)
                    .post(RequestBody.create(mapper.writeValueAsString(createRole), MediaType.parse(APPLICATION_JSON)))
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
    public Collection<RoleInfo> getRoles() throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/roles")
                    .addHeader(AUTHORIZATION, BEARER_PREFIX + accessToken)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), new TypeReference<List<RoleInfo>>(){});
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public Set<PermissionInfo> getPermissions() throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/permissions")
                    .addHeader(AUTHORIZATION, BEARER_PREFIX + accessToken)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                List<PermissionInfo> permissionInfos = mapper.readValue(response.body().string(), new TypeReference<List<PermissionInfo>>(){});
                return Set.copyOf(permissionInfos);
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void deletePermission(PermissionId permissionId) throws AuthenticationException  {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/permissions/" + permissionId.getId())
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
    public void deleteRole(RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/roles/" + roleId.getId())
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
    public ProjectInfo getInfo() throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId())
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), ProjectInfo.class);
        }
        throw new IOException();
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
    public ClientInfo getClientInfo(ClientId clientId) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId() + "/clients/" + clientId.getId())
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return mapper.readValue(response.body().string(), ClientInfo.class);
        }
        throw new IOException();
    }

    @Override
    public void createClient(CreateClient createClient) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/clients")
                    .addHeader(AUTHORIZATION, BEARER_PREFIX + accessToken)
                    .post(RequestBody.create(mapper.writeValueAsString(createClient), MediaType.parse(APPLICATION_JSON)))
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
    public void addRoleToClient(ClientId clientId, RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/clients/" + clientId.getId() + "/roles/" + roleId.getId())
                    .addHeader(AUTHORIZATION, BEARER_PREFIX + accessToken)
                    .put(RequestBody.create("{}", MediaType.parse(APPLICATION_JSON)))
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
    public void removeRoleFromClient(ClientId clientId, RoleId roleId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/clients/" + clientId.getId() + "/roles/" + roleId.getId())
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
    public void deleteClient(ClientId clientId) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/management/" + organizationId.getId() + "/" + projectId.getId() + "/clients/" + clientId.getId())
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

}
