package itx.iamservice.server.tests;

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
import itx.iamservice.core.services.dto.CreateClientRequest;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.CreatePermissionRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;
import itx.iamservice.core.services.dto.CreateUserRequest;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.SetUserNamePasswordCredentialsRequest;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.core.services.dto.UserInfo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.addPermissionToRoleForProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.addRoleToClientOnTheProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.assignRoleToUserOnProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.checkCreatedProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createAuthorization;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createClientOnTheProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createClientOnTheProjectRequest;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createNewOrganization;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createPermissionOnProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createProjectRequest;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createRoleOnProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createRoleOnProjectRequest;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createUserOnProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createUserOnProjectRequest;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.deleteRoleFromProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getClientOnTheProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getClientsOnTheProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getOrganizationInfoResponse;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getPermissionsForProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getProjectInfoResponse;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getRolesOnTheProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenResponseForUserNameAndPassword;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokensForClient;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getUserInfo;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.removeClientFromProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.removeOrganization;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.removePermissionFromProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.removePermissionFromRoleOnProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.removeProjectFromOrganization;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.removeRoleFromClientOnTheProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.removeRoleFromUserOnProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.removeUserFromProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.setUsernamePasswordCredentialsForProjectAndUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectManagementTests {

    private static String jwt;
    private static OrganizationId organizationId;
    private static ProjectId projectId;
    private static RoleId roleId;
    private static PermissionId permissionId;
    private static ClientCredentials clientCredentials;
    private static ClientId clientId;
    private static UserId userId;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void initTest() {
        jwt = getTokenResponseForUserNameAndPassword(restTemplate, port).getAccessToken();
        CreateOrganizationRequest request = new CreateOrganizationRequest(OrganizationId.from("organization-002"), "organization-002-name");
        organizationId = createNewOrganization(jwt, restTemplate, port,request);
    }

    @Test
    @Order(2)
    public void createProjectTest() {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest(ProjectId.from("project-002"), "project-002-name");
        projectId = createProject(jwt, restTemplate, port, organizationId, createProjectRequest);
        assertNotNull(projectId);
        assertNotNull(projectId.getId());
    }

    @Test
    @Order(3)
    public void createExistingProjectTest() {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest(ProjectId.from("project-002"), "project-002-name");
        ResponseEntity<ProjectId> response = createProjectRequest(jwt, restTemplate, port, organizationId, createProjectRequest);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void checkCreatedProjectTest() {
        checkCreatedProject(jwt, restTemplate, port, organizationId, projectId);
    }

    /**
     * Role related tests
     */

    @Test
    @Order(5)
    public void createRoleTest() {
        CreateRoleRequest createRoleRequest = new CreateRoleRequest(RoleId.from("role-001"), "role-001-name");
        roleId = createRoleOnProject(jwt, restTemplate, port, organizationId, projectId, createRoleRequest);
        assertNotNull(roleId);
    }

    @Test
    @Order(6)
    public void createExistingRoleTest() {
        CreateRoleRequest createRoleRequest = new CreateRoleRequest(RoleId.from("role-001"), "role-001-name");
        ResponseEntity<RoleId> response = createRoleOnProjectRequest(jwt, restTemplate, port, organizationId, projectId, createRoleRequest);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @Order(7)
    public void getRolesTest() {
        Role[] roles = getRolesOnTheProject(jwt, restTemplate, port, organizationId, projectId);
        assertNotNull(roles);
        assertNotNull(roles[0]);
        assertEquals(roleId, roles[0].getId());
    }

    /**
     * Permission Related Tests
     */

    @Test
    @Order(8)
    public void createPermissionTest() {
        CreatePermissionRequest createPermissionRequest = new CreatePermissionRequest("service", "resource", "action");
        permissionId = createPermissionOnProject(jwt, restTemplate, port, organizationId, projectId, createPermissionRequest);
        assertNotNull(permissionId);
        assertNotNull(permissionId.getId());
    }

    @Test
    @Order(9)
    public void getPermissionsTest() {
        Permission[] permissions = getPermissionsForProject(jwt, restTemplate, port, organizationId, projectId);
        assertNotNull(permissions);
        assertNotNull(permissions[0]);
        assertEquals(permissionId, permissions[0].getId());
    }

    @Test
    @Order(10)
    public void addPermissionToRoleTest() {
        addPermissionToRoleForProject(jwt, restTemplate, port, organizationId, projectId, roleId, permissionId);
    }

    /**
     * Client related tests
     */

    @Test
    @Order(11)
    public void createClientTest() {
        CreateClientRequest createClientRequest = new CreateClientRequest(ClientId.from("client-0001"), "client-name", 3600L, 7200L);
        clientCredentials = createClientOnTheProject(jwt, restTemplate, port, organizationId, projectId, createClientRequest);
        clientId = clientCredentials.getId();
        assertNotNull(clientCredentials);
        assertNotNull(clientCredentials.getId());
        assertNotNull(clientCredentials.getSecret());
    }

    @Test
    @Order(12)
    public void createExistingClientTest() {
        CreateClientRequest createClientRequest = new CreateClientRequest(ClientId.from("client-0001"), "client-name", 3600L, 7200L);
        ResponseEntity<ClientCredentials> response = createClientOnTheProjectRequest(jwt, restTemplate, port, organizationId, projectId, createClientRequest);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @Order(13)
    public void getClientTest() {
        Client client = getClientOnTheProject(jwt, restTemplate, port, organizationId, projectId, clientId);
        assertNotNull(client);
        assertEquals(clientId, client.getId());
    }

    @Test
    @Order(14)
    public void getClientsTest() {
        Client[] clients = getClientsOnTheProject(jwt, restTemplate, port, organizationId, projectId);
        assertNotNull(clients);
        assertNotNull(clients[0]);
        assertEquals(clientId, clients[0].getId());
    }

    @Test
    @Order(15)
    public void addRoleToClientTest() {
        addRoleToClientOnTheProject(jwt, restTemplate, port, organizationId, projectId, clientId, roleId);
    }

    @Test
    @Order(16)
    public void issueTokensForClient() {
        TokenResponse tokenResponse = getTokensForClient(restTemplate, port, organizationId, projectId, clientCredentials);
        assertNotNull(tokenResponse);
    }

    /**
     * User related tests
     */

    @Test
    @Order(17)
    public void createUserTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest(UserId.from("user-001"), "user-001-name", 3600L, 3600L);
        userId = createUserOnProject(jwt, restTemplate, port, organizationId, projectId, createUserRequest);
        assertNotNull(userId);
    }

    @Test
    @Order(18)
    public void createExistingUserTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest(UserId.from("user-001"), "user-001-name", 3600L, 3600L);
        ResponseEntity<UserId> response = createUserOnProjectRequest(jwt, restTemplate, port, organizationId, projectId, createUserRequest);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @Order(19)
    public void addRoleToUserTest() {
        assignRoleToUserOnProject(jwt, restTemplate, port, organizationId, projectId, userId, roleId);
    }

    @Test
    @Order(20)
    public void checkUserTest() {
        UserInfo userInfo = getUserInfo(restTemplate, port, organizationId, projectId, userId);
        assertNotNull(userInfo);
        assertEquals(userId, userInfo.getId());
    }

    @Test
    @Order(21)
    public void setUsernamePasswordCredentials() {
        SetUserNamePasswordCredentialsRequest setUserNamePasswordCredentialsRequest = new SetUserNamePasswordCredentialsRequest(userId.getId(), "secret-01");
        setUsernamePasswordCredentialsForProjectAndUser(jwt, restTemplate, port, organizationId, projectId, userId, setUserNamePasswordCredentialsRequest);
    }

    @Test
    @Order(22)
    public void issueTokensForUser() {
        TokenResponse tokenResponse = getTokenResponseForUserNameAndPassword(restTemplate, port, userId.getId(), "secret-01",
                clientCredentials.getId(), clientCredentials.getSecret(), organizationId, projectId);
        assertNotNull(tokenResponse);
    }

    @Test
    @Order(23)
    public void removeRoleFromUserTest() {
        removeRoleFromUserOnProject(jwt, restTemplate, port, organizationId, projectId, userId, roleId);
    }

    @Test
    @Order(24)
    public void deleteUserTest() {
        removeUserFromProject(jwt, restTemplate, port, organizationId, projectId, userId);
    }

    /**
     * Cleanup after testing, delete operations
     */

    @Test
    @Order(25)
    public void removeRoleFromClientTest() {
        removeRoleFromClientOnTheProject(jwt, restTemplate, port, organizationId, projectId, clientId, roleId);
    }

    @Test
    @Order(26)
    public void removeClientTest() {
        removeClientFromProject(jwt, restTemplate, port, organizationId, projectId, clientId);
    }

    @Test
    @Order(27)
    public void removePermissionFromRole() {
        removePermissionFromRoleOnProject(jwt, restTemplate, port, organizationId, projectId, roleId, permissionId);
    }


    @Test
    @Order(28)
    public void deletePermissionsTest() {
        removePermissionFromProject(jwt, restTemplate, port, organizationId, projectId, permissionId);
    }

    @Test
    @Order(29)
    public void deleteRoleTest() {
        deleteRoleFromProject(jwt, restTemplate, port, organizationId, projectId, roleId);
    }


    @Test
    @Order(30)
    public void removeProjectTest() {
        removeProjectFromOrganization(jwt, restTemplate, port, organizationId, projectId);
    }

    @Test
    @Order(31)
    public void checkRemovedProjectTest() {
        ResponseEntity<ProjectInfo> response = getProjectInfoResponse(restTemplate, port, organizationId, projectId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(32)
    public void removeOrganizationTest() {
        removeOrganization(jwt, restTemplate, port, organizationId);
    }

    @Test
    @Order(33)
    public void checkRemovedOrganization() {
        ResponseEntity<OrganizationInfo> response = getOrganizationInfoResponse(restTemplate, port, organizationId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
