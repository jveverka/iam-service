package itx.iamservice.server.tests;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.deleteOrganizationRecursively;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenResponseForIAMAdmins;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenResponseForUserNameAndPassword;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.setupOrganization;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminServicesTests {

    private static final Logger LOG = LoggerFactory.getLogger(AdminServicesTests.class);

    private static String jwt_admin_token;
    private static OrganizationId organizationId = OrganizationId.from("my-org-001");
    private static ProjectId projectId = ProjectId.from("project-001");
    private static ClientId adminClientId = ClientId.from("acl-001");
    private static String adminClientSecret = "acl-secret";
    private static UserId adminUserId = UserId.from("admin");
    private static String adminPassword = "secret";
    private static String jwt_organization_admin_token;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void initTest() {
        jwt_admin_token = getTokenResponseForIAMAdmins(restTemplate, port).getAccessToken();
        LOG.info("JSW  access_token: {}", jwt_admin_token);
    }

    @Test
    @Order(2)
    public void createNewOrganizationWithAdminUser() {
        Set<String> projectAudience = new HashSet<>();
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "My Organization 001",
                projectId.getId(), "My Project 001",
                adminClientId.getId(), adminClientSecret,
                adminUserId.getId(), adminPassword, projectAudience);
        ResponseEntity<SetupOrganizationResponse> setupOrganizationResponseResponseEntity = setupOrganization(restTemplate, port, jwt_admin_token, setupOrganizationRequest);
        assertEquals(HttpStatus.OK, setupOrganizationResponseResponseEntity.getStatusCode());
    }

    /**
    @Test
    @Order(3)
    public void getTokenOrganizationForAdminUser() {
        jwt_organization_admin_token = getTokenResponseForUserNameAndPassword(restTemplate, port, adminUserId.getId(), adminPassword,
                adminClientId, adminClientSecret, organizationId, projectId).getAccessToken();
        assertNotNull(jwt_organization_admin_token);
    }
    */

    @Test
    @Order(10)
    public void cleanupProjectTest() {
        ResponseEntity<Void> response = deleteOrganizationRecursively(restTemplate, port,  jwt_admin_token, organizationId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
