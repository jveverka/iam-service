package itx.iamservice.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectManagementTests {

    private static OrganizationId organizationId;
    private static ProjectId projectId;
    private static RoleId roleId;
    private static PermissionId permissionId;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    @Order(1)
    public void initTest() {
        CreateOrganizationRequest createOrganizationTest = new CreateOrganizationRequest("organization-002");
        ResponseEntity<OrganizationId> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/management/organizations",
                createOrganizationTest, OrganizationId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        organizationId = response.getBody();
    }

    @Test
    @Order(2)
    public void createProjectTest() {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest("project-002");
        ResponseEntity<ProjectId> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects",
                createProjectRequest, ProjectId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        projectId = response.getBody();
        assertNotNull(projectId);
        assertNotNull(projectId.getId());
    }

    @Test
    @Order(3)
    public void checkCreatedProjectTest() {
        ResponseEntity<ProjectInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId(), ProjectInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProjectInfo projectInfo = response.getBody();
        assertNotNull(projectInfo);
        assertEquals(projectId, projectInfo.getId());
    }

    @Test
    @Order(4)
    public void createRoleTest() {
        CreateRoleRequest createRoleRequest = new CreateRoleRequest("role-001");
        ResponseEntity<RoleId> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles",
                createRoleRequest, RoleId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        roleId = response.getBody();
        assertNotNull(roleId);
    }

    @Test
    @Order(5)
    public void getRolesTest() {
        ResponseEntity<Role[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles",
                Role[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Role[] roles = response.getBody();
        assertNotNull(roles);
        assertNotNull(roles[0]);
        assertEquals(roleId, roles[0].getId());
    }

    @Test
    @Order(6)
    public void createPermissionTest() {
        CreatePermissionRequest createPermissionRequest = new CreatePermissionRequest("service", "resource", "action");
        ResponseEntity<PermissionId> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions",
                createPermissionRequest, PermissionId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        permissionId = response.getBody();
        assertNotNull(permissionId);
        assertNotNull(permissionId.getId());
    }

    @Test
    @Order(7)
    public void getPermissionsTest() {
        ResponseEntity<Permission[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions",
                Permission[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Permission[] permissions = response.getBody();
        assertNotNull(permissions);
        assertNotNull(permissions[0]);
        assertEquals(permissionId, permissions[0].getId());
    }

    @Test
    @Order(9)
    public void addPermissionToRoleTest() {
        restTemplate.put(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles-permissions/" + roleId.getId() + "/" + permissionId.getId(), null);
    }

    @Test
    @Order(10)
    public void removePermissionFromRole() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles-permissions/" + roleId.getId() + "/" + permissionId.getId());

    }


    @Test
    @Order(11)
    public void deletePermissionsTest() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/permissions/" + permissionId.getId());
    }

    @Test
    @Order(12)
    public void deleteRoleTest() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles/" + roleId.getId());
    }


    @Test
    @Order(13)
    public void removeProjectTest() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId());
    }

    @Test
    @Order(14)
    public void checkRemovedProjectTest() {
        ResponseEntity<ProjectInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId(), ProjectInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(15)
    public void shutdownTest() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/organizations/" + organizationId.getId());
    }

}
