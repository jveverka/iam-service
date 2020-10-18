package itx.iamservice.client.spring.httpclient;

import itx.iamservice.core.dto.CreateOrganization;
import itx.iamservice.core.dto.CreateProject;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.ClientInfo;
import itx.iamservice.core.services.dto.CreateClientRequest;
import itx.iamservice.core.services.dto.CreatePermissionRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;
import itx.iamservice.core.services.dto.CreateUserRequest;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.SetUserNamePasswordCredentialsRequest;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.core.services.dto.UserInfoResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class HttpClientTestUtils {

    private HttpClientTestUtils() {
    }

    public static ResponseEntity<String> getActuatorInfo(TestRestTemplate restTemplate, int port) {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/info", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response;
    }

    public static ResponseEntity<IntrospectResponse> getTokenVerificationResponse(TestRestTemplate restTemplate, int port, String token) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("token", token);
        return restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/introspect?token={token}",
                null, IntrospectResponse.class, urlVariables);
    }

    public static ResponseEntity<Void> getTokenRevokeResponse(TestRestTemplate restTemplate, int port, String token) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("token", token);
        return restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/revoke?token={token}",
                null, Void.class, urlVariables);
    }

    public static HttpHeaders createAuthorization(String jwt) {
        HttpHeaders requestHeaders = new HttpHeaders();
        if (jwt != null) {
            String authorizationHeader = "Bearer " + jwt;
            requestHeaders.add("Authorization", authorizationHeader);
        }
        return requestHeaders;
    }

    public static OrganizationId createNewOrganization(String jwt, TestRestTemplate restTemplate, int port, CreateOrganization request) {
        ResponseEntity<OrganizationId> response = createNewOrganizationResponse(jwt, restTemplate, port, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationId organizationId = response.getBody();
        assertNotNull(organizationId);
        assertNotNull(organizationId.getId());
        return organizationId;
    }

    public static ResponseEntity<OrganizationId> createNewOrganizationResponse(String jwt, TestRestTemplate restTemplate, int port, CreateOrganization request) {
        HttpEntity<CreateOrganization> requestEntity = new HttpEntity<>(request, createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/organizations/",
                HttpMethod.POST,
                requestEntity,
                OrganizationId.class);
    }

    public static void checkOrganizationCount(String jwt, TestRestTemplate restTemplate, int port, int expectedCount) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<OrganizationInfo[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery",
                HttpMethod.GET,
                requestEntity,
                OrganizationInfo[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo[] organizationInfos = response.getBody();
        assertNotNull(organizationInfos);
        assertEquals(expectedCount, organizationInfos.length);
    }

    public static void checkOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<OrganizationInfo> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId(),
                HttpMethod.GET,
                requestEntity,
                OrganizationInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo organizationInfo = response.getBody();
        assertNotNull(organizationInfo);
        assertEquals(organizationId, organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getName());
        assertNotNull(organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getProjects());
        assertNotNull(organizationInfo.getX509Certificate());
    }

    public static ResponseEntity<Void> removeOrganizationResponse(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/organizations/" + organizationId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
    }

    public static void removeOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/organizations/" + organizationId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void checkRemovedOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<OrganizationInfo> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId(),
                HttpMethod.GET,
                requestEntity,
                OrganizationInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    public static TokenResponse getTokenResponseForIAMAdmins(TestRestTemplate restTemplate, int port) {
        return getTokenResponseForUserNameAndPassword(restTemplate, port, ModelUtils.IAM_ADMIN_USER.getId(), "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret", ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
    }

    public static ResponseEntity<SetupOrganizationResponse> setupOrganization(TestRestTemplate restTemplate, int port, String jwt, SetupOrganizationRequest request) {
        HttpEntity<SetupOrganizationRequest> requestEntity = new HttpEntity<>(request, createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/organization/setup",
                HttpMethod.POST,
                requestEntity,
                SetupOrganizationResponse.class);
    }

    public static ResponseEntity<Void> deleteOrganizationRecursively(TestRestTemplate restTemplate, int port, String jwt, OrganizationId organizationId) {
        HttpEntity<SetupOrganizationRequest> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/organization/" + organizationId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class);
    }

    public static ResponseEntity<Void> deleteProjectRecursively(TestRestTemplate restTemplate, int port, String jwt, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<SetupOrganizationRequest> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/organization/" + organizationId.getId() + "/" + projectId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class);
    }

    public static TokenResponse getTokenResponseForUserNameAndPassword(TestRestTemplate restTemplate, int port, String userName, String password, ClientId clientId, String clientSecret, OrganizationId organizationId, ProjectId projectId) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("grant_type", "password");
        urlVariables.put("username", userName);
        urlVariables.put("password", password);
        urlVariables.put("scope", "");
        urlVariables.put("client_id", clientId.getId());
        urlVariables.put("client_secret", clientSecret);
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/token" +
                        "?grant_type={grant_type}&username={username}&scope={scope}&password={password}&client_id={client_id}&client_secret={client_secret}",
                null, TokenResponse.class, urlVariables);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static TokenResponse getTokensForClient(TestRestTemplate restTemplate, int port) {
        ClientCredentials clientCredentials = new ClientCredentials(ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        return getTokensForClient(restTemplate, port, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, clientCredentials);
    }

    public static TokenResponse getTokensForClient(TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientCredentials credentials) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("grant_type", "client_credentials");
        urlVariables.put("scope", "");
        urlVariables.put("client_id", credentials.getId().getId());
        urlVariables.put("client_secret", credentials.getSecret());
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/" + organizationId.getId() + "/"  +  projectId.getId() + "/token?grant_type={grant_type}&scope={scope}&client_id={client_id}&client_secret={client_secret}",
                null, TokenResponse.class, urlVariables);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }


    public static ResponseEntity<ProjectId> createProjectRequest(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, CreateProject request) {
        HttpEntity<CreateProject> requestEntity = new HttpEntity<>(request, createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects",
                HttpMethod.POST,
                requestEntity,
                ProjectId.class);
    }

    public static ProjectId createProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, CreateProject request) {
        ResponseEntity<ProjectId> response = createProjectRequest(jwt, restTemplate, port, organizationId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static void checkCreatedProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<ProjectInfo> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId(),
                HttpMethod.GET,
                requestEntity,
                ProjectInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProjectInfo projectInfo = response.getBody();
        assertNotNull(projectInfo);
        assertEquals(projectId, projectInfo.getId());
    }

    public static ResponseEntity<RoleId> createRoleOnProjectRequest(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreateRoleRequest request) {
        HttpEntity<CreateRoleRequest> requestEntity = new HttpEntity<>(request, createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles",
                HttpMethod.POST,
                requestEntity,
                RoleId.class);
    }

    public static RoleId createRoleOnProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreateRoleRequest request) {
        ResponseEntity<RoleId> response = createRoleOnProjectRequest(jwt,  restTemplate,  port,  organizationId,  projectId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static Role[] getRolesOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Role[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles",
                HttpMethod.GET,
                requestEntity,
                Role[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static PermissionId createPermissionOnProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreatePermissionRequest createPermissionRequest) {
        HttpEntity<CreatePermissionRequest> requestEntity = new HttpEntity<>(createPermissionRequest, createAuthorization(jwt));
        ResponseEntity<PermissionId> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions",
                HttpMethod.POST,
                requestEntity,
                PermissionId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static Permission[] getPermissionsForProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Permission[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions",
                HttpMethod.GET,
                requestEntity,
                Permission[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static void addPermissionToRoleForProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles-permissions/" + roleId.getId() + "/" + permissionId.getId(),
                HttpMethod.PUT,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public static ResponseEntity<ClientCredentials> createClientOnTheProjectRequest(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreateClientRequest request) {
        HttpEntity<CreateClientRequest> requestEntity = new HttpEntity<>(request, createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients",
                HttpMethod.POST,
                requestEntity,
                ClientCredentials.class);
    }

    public static ClientCredentials createClientOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreateClientRequest request) {
        ResponseEntity<ClientCredentials> response = createClientOnTheProjectRequest(jwt, restTemplate, port, organizationId, projectId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static Client getClientOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId  clientId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Client> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId(),
                HttpMethod.GET,
                requestEntity,
                Client.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static Client[] getClientsOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Client[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients",
                HttpMethod.GET,
                requestEntity,
                Client[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static void addRoleToClientOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId clientId, RoleId roleId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.PUT,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void removeRoleFromClientOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId clientId, RoleId roleId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void removeClientFromProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static ResponseEntity<UserId> createUserOnProjectRequest(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreateUserRequest request) {
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(request, createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/users",
                HttpMethod.POST,
                requestEntity,
                UserId.class);
    }

    public static UserId createUserOnProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreateUserRequest request) {
        ResponseEntity<UserId> response = createUserOnProjectRequest(jwt, restTemplate, port, organizationId, projectId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static void removeUserFromProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, UserId userId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/users/" + userId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void setUsernamePasswordCredentialsForProjectAndUser(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, UserId userId, SetUserNamePasswordCredentialsRequest setUserNamePasswordCredentialsRequest) {
        HttpEntity<SetUserNamePasswordCredentialsRequest> requestEntity = new HttpEntity<>(setUserNamePasswordCredentialsRequest, createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/users/" + userId.getId() + "/credentials-username-password/",
                HttpMethod.PUT,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void assignRoleToUserOnProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, UserId userId, RoleId roleId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/users/" + userId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.PUT,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void removeRoleFromUserOnProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, UserId userId, RoleId roleId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/users/" + userId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static OrganizationInfo[] getOrganizationInfos(TestRestTemplate restTemplate, int port) {
        ResponseEntity<OrganizationInfo[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/", OrganizationInfo[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static OrganizationInfo getOrganizationInfo(TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        ResponseEntity<OrganizationInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId(), OrganizationInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static ResponseEntity<OrganizationInfo> getOrganizationInfoResponse(TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        return restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId(), OrganizationInfo.class);
    }

    public static ProjectInfo getProjectInfo(TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        ResponseEntity<ProjectInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId(), ProjectInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static UserInfo getUserInfo(TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, UserId userId) {
        ResponseEntity<UserInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId() + "/users/" + userId.getId(), UserInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static ClientInfo getClientInfo(TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        ResponseEntity<ClientInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId() + "/clients/" + clientId.getId(), ClientInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static ResponseEntity<ProjectInfo> getProjectInfoResponse(TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        return restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId(), ProjectInfo.class);
    }

    public static void removePermissionFromRoleOnProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles-permissions/" + roleId.getId() + "/" + permissionId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public static void removePermissionFromProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, PermissionId permissionId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions/" + permissionId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void deleteRoleFromProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, RoleId roleId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void removeProjectFromOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        ResponseEntity<Void> response = removeProject(jwt, restTemplate, port, organizationId, projectId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static ResponseEntity<Void> removeProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/admin/" + organizationId.getId() + "/projects/" + projectId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
    }

    public static ResponseEntity<UserInfoResponse> getUserInfo(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<UserInfoResponse> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        return restTemplate.exchange(
                "http://localhost:" + port + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/userinfo",
                HttpMethod.GET,
                requestEntity,
                UserInfoResponse.class
        );
    }

}
