package itx.iamservice.server.tests;

import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.UserInfo;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiscoveryAPIsTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void getOrganizationsInfoTest() {
        ResponseEntity<OrganizationInfo[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/organizations", OrganizationInfo[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo[] organizationInfo = response.getBody();
        assertNotNull(organizationInfo);
        assertTrue(organizationInfo.length > 0);
        assertNotNull(organizationInfo[0]);
        assertNotNull(organizationInfo[0].getName());
        assertNotNull(organizationInfo[0].getOrganizationId());
        assertNotNull(organizationInfo[0].getProjects());
        assertNotNull(organizationInfo[0].getX509Certificate());
    }

    @Test
    @Order(2)
    public void getOrganizationInfoTest() {
        ResponseEntity<OrganizationInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/organizations/" + ModelUtils.IAM_ADMINS_ORG.getId(), OrganizationInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo organizationInfo = response.getBody();
        assertNotNull(organizationInfo);
        assertNotNull(organizationInfo.getName());
        assertNotNull(organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getProjects());
        assertNotNull(organizationInfo.getX509Certificate());
    }

    @Test
    @Order(3)
    public void getProjectInfoTest() {
        String organizationId = ModelUtils.IAM_ADMINS_ORG.getId();
        String projectId = ModelUtils.IAM_ADMINS_PROJECT.getId();
        ResponseEntity<ProjectInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId + "/" + projectId, ProjectInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProjectInfo projectInfo = response.getBody();
        assertNotNull(projectInfo);
        assertNotNull(projectInfo.getId());
        assertNotNull(projectInfo.getName());
        assertNotNull(projectInfo.getUsers());
        assertNotNull(projectInfo.getClients());
        assertNotNull(projectInfo.getOrganizationCertificate());
        assertNotNull(projectInfo.getOrganizationId());
        assertNotNull(projectInfo.getProjectCertificate());
    }

    @Test
    @Order(4)
    public void getUserInfoTest() {
        String userId = ModelUtils.IAM_ADMIN_USER.getId();
        String organizationId = ModelUtils.IAM_ADMINS_ORG.getId();
        String projectId = ModelUtils.IAM_ADMINS_PROJECT.getId();
        ResponseEntity<UserInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/" + organizationId + "/" + projectId + "/" + userId, UserInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserInfo userInfo = response.getBody();
        assertNotNull(userInfo);
        assertNotNull(userInfo.getId());
        assertNotNull(userInfo.getName());
        assertNotNull(userInfo.getRoles());
        assertNotNull(userInfo.getOrganizationId());
        assertNotNull(userInfo.getOrganizationCertificate());
        assertNotNull(userInfo.getProjectCertificate());
        assertNotNull(userInfo.getUserCertificate());
        assertNotNull(userInfo.getProjectId());
    }

}