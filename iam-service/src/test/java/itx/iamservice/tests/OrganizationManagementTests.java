package itx.iamservice.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.OrganizationInfo;
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
public class OrganizationManagementTests {

    private static OrganizationId organizationId;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void createOrganizationTest() {
        CreateOrganizationRequest createOrganizationTest = new CreateOrganizationRequest("organization-001");
        ResponseEntity<OrganizationId> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/management/organizations",
                createOrganizationTest, OrganizationId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        organizationId = response.getBody();
        assertNotNull(organizationId);
        assertNotNull(organizationId.getId());
    }

    @Test
    @Order(2)
    public void getOrganizationTest() {
        ResponseEntity<OrganizationInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/organizations/" + organizationId.getId(), OrganizationInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo organizationInfo = response.getBody();
        assertNotNull(organizationInfo);
        assertEquals(organizationId, organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getName());
        assertNotNull(organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getProjects());
        assertNotNull(organizationInfo.getX509Certificate());
    }

    @Test
    @Order(3)
    public void removeOrganizationTest() {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/organizations/" + organizationId.getId());
    }

    @Test
    @Order(4)
    public void checkRemovedOrganizationTest() {
        ResponseEntity<OrganizationInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/organizations/" + organizationId.getId(), OrganizationInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
