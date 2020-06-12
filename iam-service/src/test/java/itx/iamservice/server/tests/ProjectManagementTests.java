package itx.iamservice.server.tests;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.dto.CreateClientRequest;
import itx.iamservice.core.services.dto.CreatePermissionRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;
import itx.iamservice.core.services.dto.ProjectInfo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static itx.iamservice.server.tests.TestUtils.checkCreatedProject;
import static itx.iamservice.server.tests.TestUtils.createAuthorization;
import static itx.iamservice.server.tests.TestUtils.createNewOrganization;
import static itx.iamservice.server.tests.TestUtils.createPermissionOnProject;
import static itx.iamservice.server.tests.TestUtils.createProject;
import static itx.iamservice.server.tests.TestUtils.createRoleOnProject;
import static itx.iamservice.server.tests.TestUtils.getPermissionsForProject;
import static itx.iamservice.server.tests.TestUtils.getRolesOnTheProject;
import static itx.iamservice.server.tests.TestUtils.getTokenResponseForUserNameAndPassword;
import static itx.iamservice.server.tests.TestUtils.removeOrganization;
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
    private static ClientId clientId;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void initTest() {
        jwt = getTokenResponseForUserNameAndPassword(restTemplate, port).getAccessToken();
        organizationId = createNewOrganization(jwt, restTemplate, port,"organization-002");
    }

    @Test
    @Order(2)
    public void createProjectTest() {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest("project-002");
        projectId = createProject(jwt, restTemplate, port, organizationId, createProjectRequest);
        assertNotNull(projectId);
        assertNotNull(projectId.getId());
    }

    @Test
    @Order(3)
    public void checkCreatedProjectTest() {
        checkCreatedProject(jwt, restTemplate, port, organizationId, projectId);
    }

    /**
     * Role related tests
     */

    @Test
    @Order(4)
    public void createRoleTest() {
        CreateRoleRequest createRoleRequest = new CreateRoleRequest("role-001");
        roleId = createRoleOnProject(jwt, restTemplate, port, organizationId, projectId, createRoleRequest);
        assertNotNull(roleId);
    }

    @Test
    @Order(5)
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
    @Order(6)
    public void createPermissionTest() {
        CreatePermissionRequest createPermissionRequest = new CreatePermissionRequest("service", "resource", "action");
        permissionId = createPermissionOnProject(jwt, restTemplate, port, organizationId, projectId, createPermissionRequest);
        assertNotNull(permissionId);
        assertNotNull(permissionId.getId());
    }

    @Test
    @Order(7)
    public void getPermissionsTest() {
        Permission[] permissions = getPermissionsForProject(jwt, restTemplate, port, organizationId, projectId);
        assertNotNull(permissions);
        assertNotNull(permissions[0]);
        assertEquals(permissionId, permissions[0].getId());
    }

    @Test
    @Order(9)
    public void addPermissionToRoleTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles-permissions/" + roleId.getId() + "/" + permissionId.getId(),
                HttpMethod.PUT,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Client related tests
     */

    @Test
    @Order(10)
    public void createClientTest() {
        CreateClientRequest createClientRequest = new CreateClientRequest("client-name", 3600L, 7200L);
        HttpEntity<CreateClientRequest> requestEntity = new HttpEntity<>(createClientRequest, createAuthorization(jwt));
        ResponseEntity<ClientId> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients",
                HttpMethod.POST,
                requestEntity,
                ClientId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        clientId = response.getBody();
        assertNotNull(clientId);
    }


    @Test
    @Order(11)
    public void getClientTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Client> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId(),
                HttpMethod.GET,
                requestEntity,
                Client.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Client clients = response.getBody();
        assertNotNull(clients);
        assertEquals(clientId, clients.getId());
    }

    @Test
    @Order(12)
    public void getClientsTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Client[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients",
                HttpMethod.GET,
                requestEntity,
                Client[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Client[] clients = response.getBody();
        assertNotNull(clients);
        assertNotNull(clients[0]);
        assertEquals(clientId, clients[0].getId());
    }

    @Test
    @Order(13)
    public void addRoleToClientTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.PUT,
                requestEntity,
                Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(14)
    public void removeRoleFromClientTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(15)
    public void removeClientTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/clients/" + clientId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * Cleanup after testing, delete operations
     */

    @Test
    @Order(16)
    public void removePermissionFromRole() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles-permissions/" + roleId.getId() + "/" + permissionId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    @Order(17)
    public void deletePermissionsTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions/" + permissionId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(18)
    public void deleteRoleTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles/" + roleId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    @Order(19)
    public void removeProjectTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(20)
    public void checkRemovedProjectTest() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<ProjectInfo> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId(),
                HttpMethod.GET,
                requestEntity,
                ProjectInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(21)
    public void shutdownTest() {
        removeOrganization(jwt, restTemplate, port, organizationId);
    }

}
