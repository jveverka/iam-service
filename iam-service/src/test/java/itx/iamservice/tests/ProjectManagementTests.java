package itx.iamservice.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.services.dto.CreateOrganizationRequest;
import itx.iamservice.services.dto.CreateProjectRequest;
import itx.iamservice.services.dto.CreateRoleRequest;
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

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectManagementTests {

    private static OrganizationId organizationId;
    private static ProjectId projectId;
    private static RoleId roleId;

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
    public void deleteRoleTest() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId() + "/roles/" + roleId.getId());
    }


    @Test
    @Order(7)
    public void removeProjectTest() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/" + organizationId.getId() + "/projects/" + projectId.getId());
    }

    @Test
    @Order(8)
    public void checkRemovedProjectTest() {
        ResponseEntity<ProjectInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId.getId() + "/" + projectId.getId(), ProjectInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(9)
    public void shutdownTest() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/organizations/" + organizationId.getId());
    }

}
