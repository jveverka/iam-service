package itx.iamservice.server.tests;

import itx.iamservice.core.dto.CreateOrganization;
import itx.iamservice.core.model.OrganizationId;
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

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminOrganizationTests {

    private static String jwt_admin;
    private static OrganizationId organizationId01;
    private static OrganizationId organizationId02;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void init() {
        jwt_admin = getTokenResponseForIAMAdmins(restTemplate, port).getAccessToken();
    }

    @Test
    @Order(2)
    public void createFirstOrganizationTest() {
        CreateOrganization request = new CreateOrganization("organization-001", "organization-001-name");
        organizationId01 = createNewOrganization(jwt_admin, restTemplate, port,request);
        checkOrganizationCount(jwt_admin, restTemplate, port,2);
        checkOrganization(jwt_admin, restTemplate, port, organizationId01);
    }

    @Test
    @Order(3)
    public void createSecondOrganizationTest() {
        CreateOrganization request = new CreateOrganization("organization-002", "organization-002-name");
        organizationId02 = createNewOrganization(jwt_admin, restTemplate, port,request);
        checkOrganizationCount(jwt_admin, restTemplate, port, 3);
        checkOrganization(jwt_admin, restTemplate, port, organizationId02);
    }

    @Test
    @Order(4)
    public void createExistingOrganizationTest() {
        CreateOrganization request = new CreateOrganization("organization-002", "organization-002-name");
        ResponseEntity<OrganizationId> response = createNewOrganizationResponse(jwt_admin, restTemplate, port, request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @Order(5)
    public void removeFirstOrganizationTest() {
        removeOrganization(jwt_admin, restTemplate, port, organizationId01);
        checkOrganizationCount(jwt_admin, restTemplate, port, 2);
        checkOrganization(jwt_admin, restTemplate, port, organizationId02);
        checkRemovedOrganization(jwt_admin, restTemplate, port, organizationId01);
    }

    @Test
    @Order(6)
    public void removeSecondOrganizationTest() {
        removeOrganization(jwt_admin, restTemplate, port,  organizationId02);
        checkOrganizationCount(jwt_admin, restTemplate, port,1);
        checkRemovedOrganization(jwt_admin, restTemplate, port, organizationId01);
        checkRemovedOrganization(jwt_admin, restTemplate, port, organizationId02);
    }

    @Test
    @Order(7)
    public void createOrganizationNoTokenTest() {
        CreateOrganization request = new CreateOrganization("organization-001", "organization-001-name");
        ResponseEntity<OrganizationId> response = createNewOrganizationResponse(null, restTemplate, port, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(8)
    public void removeOrganizationNoTokenTest() {
        ResponseEntity<Void> response = removeOrganizationResponse(null, restTemplate, port,  organizationId02);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
