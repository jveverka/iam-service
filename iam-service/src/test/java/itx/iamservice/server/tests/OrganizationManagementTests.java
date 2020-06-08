package itx.iamservice.server.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.TokenResponse;
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

import static itx.iamservice.server.tests.TestUtils.checkOrganization;
import static itx.iamservice.server.tests.TestUtils.checkOrganizationCount;
import static itx.iamservice.server.tests.TestUtils.checkRemovedOrganization;
import static itx.iamservice.server.tests.TestUtils.createNewOrganization;
import static itx.iamservice.server.tests.TestUtils.getTokenResponseForUserNameAndPassword;
import static itx.iamservice.server.tests.TestUtils.removeOrganization;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationManagementTests {

    private static String jwt;
    private static OrganizationId organizationId01;
    private static OrganizationId organizationId02;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void createFirstOrganizationTest() {
        jwt = getTokenResponseForUserNameAndPassword(restTemplate, port).getAccessToken();
        organizationId01 = createNewOrganization(jwt, restTemplate, port,"organization-001");
        checkOrganizationCount(jwt, restTemplate, port,2);
        checkOrganization(jwt, restTemplate, port, organizationId01);
    }

    @Test
    @Order(2)
    public void createSecondOrganizationTest() {
        organizationId02 = createNewOrganization(jwt, restTemplate, port,"organization-002");
        checkOrganizationCount(jwt, restTemplate, port, 3);
        checkOrganization(jwt, restTemplate, port, organizationId02);
    }

    @Test
    @Order(3)
    public void removeFirstOrganizationTest() {
        removeOrganization(jwt, restTemplate, port, organizationId01);
        checkOrganizationCount(jwt, restTemplate, port, 2);
        checkOrganization(jwt, restTemplate, port, organizationId02);
        checkRemovedOrganization(jwt, restTemplate, port, organizationId01);
    }

    @Test
    @Order(4)
    public void removeSecondOrganizationTest() {
        removeOrganization(jwt, restTemplate, port,  organizationId02);
        checkOrganizationCount(jwt, restTemplate, port,1);
        checkRemovedOrganization(jwt, restTemplate, port, organizationId01);
        checkRemovedOrganization(jwt, restTemplate, port, organizationId02);
    }

}
