package itx.iamservice.server.tests;

import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.CreateClientRequest;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.CreatePermissionRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.TokenResponse;
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
        String authorizationHeader = "Bearer " + jwt;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", authorizationHeader);
        return requestHeaders;
    }

    public static OrganizationId createNewOrganization(String jwt, TestRestTemplate restTemplate, int port, String name) {
        CreateOrganizationRequest createOrganizationTest = new CreateOrganizationRequest(name);
        HttpEntity<CreateOrganizationRequest> requestEntity = new HttpEntity<>(createOrganizationTest, createAuthorization(jwt));

        ResponseEntity<OrganizationId> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/organizations",
                HttpMethod.POST,
                requestEntity,
                OrganizationId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationId organizationId = response.getBody();
        assertNotNull(organizationId);
        assertNotNull(organizationId.getId());
        return organizationId;
    }

    public static void checkOrganizationCount(String jwt, TestRestTemplate restTemplate, int port, int expectedCount) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<OrganizationInfo[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/organizations",
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
                "http://localhost:" + port + "/services/discovery/organizations/" + organizationId.getId(),
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

    public static void removeOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/organizations/" + organizationId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void checkRemovedOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<OrganizationInfo> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/organizations/" + organizationId.getId(),
                HttpMethod.GET,
                requestEntity,
                OrganizationInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    public static TokenResponse getTokenResponseForUserNameAndPassword(TestRestTemplate restTemplate, int port) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("grant_type", "password");
        urlVariables.put("username", ModelUtils.IAM_ADMIN_USER.getId());
        urlVariables.put("password", "secret");
        urlVariables.put("scope", "");
        urlVariables.put("client_id", "admin-client");
        urlVariables.put("client_secret", "top-secret");
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/token" +
                        "?grant_type={grant_type}&username={username}&scope={scope}&password={password}&client_id={client_id}&client_secret={client_secret}",
                null, TokenResponse.class, urlVariables);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static ProjectId createProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, CreateProjectRequest createProjectRequest) {
        HttpEntity<CreateProjectRequest> requestEntity = new HttpEntity<>(createProjectRequest, createAuthorization(jwt));
        ResponseEntity<ProjectId> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects",
                HttpMethod.POST,
                requestEntity,
                ProjectId.class);
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

    public static RoleId createRoleOnProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreateRoleRequest createRoleRequest) {
        HttpEntity<CreateRoleRequest> requestEntity = new HttpEntity<>(createRoleRequest, createAuthorization(jwt));
        ResponseEntity<RoleId> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles",
                HttpMethod.POST,
                requestEntity,
                RoleId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static Role[] getRolesOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Role[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles",
                HttpMethod.GET,
                requestEntity,
                Role[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static PermissionId createPermissionOnProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreatePermissionRequest createPermissionRequest) {
        HttpEntity<CreatePermissionRequest> requestEntity = new HttpEntity<>(createPermissionRequest, createAuthorization(jwt));
        ResponseEntity<PermissionId> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions",
                HttpMethod.POST,
                requestEntity,
                PermissionId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static Permission[] getPermissionsForProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Permission[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions",
                HttpMethod.GET,
                requestEntity,
                Permission[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static void addPermissionToRoleForProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, RoleId roleId, PermissionId permissionId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles-permissions/" + roleId.getId() + "/" + permissionId.getId(),
                HttpMethod.PUT,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public static ClientId createClientOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, CreateClientRequest createClientRequest) {
        HttpEntity<CreateClientRequest> requestEntity = new HttpEntity<>(createClientRequest, createAuthorization(jwt));
        ResponseEntity<ClientId> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients",
                HttpMethod.POST,
                requestEntity,
                ClientId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static Client getClientOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId  clientId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Client> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId(),
                HttpMethod.GET,
                requestEntity,
                Client.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static Client[] getClientsOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Client[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients",
                HttpMethod.GET,
                requestEntity,
                Client[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    public static void addRoleToClientOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId clientId, RoleId roleId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.PUT,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void removeRoleFromClientOnTheProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId clientId, RoleId roleId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public static void removeClientFromProject(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId, ProjectId projectId, ClientId clientId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

}
