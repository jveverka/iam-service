package itx.iamservice.examples.methodsecurity.ittests;


import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.examples.methodsecurity.dto.SystemInfo;
import itx.iamservice.serviceclient.IAMAuthorizerClient;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import itx.iamservice.serviceclient.IAMServiceManagerClient;
import itx.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MethodSecurityTestsIT {

    private static TestRestTemplate restTemplate;
    private static int iamServerPort;
    private static int resourceServerPort;
    private static TokenResponse adminTokens;
    private static TokenResponse userTokens;

    private static OrganizationId organizationId;
    private static ProjectId projectId;
    private static UserId userId;
    private static ClientId clientId;

    private static IAMServiceManagerClient iamServiceManagerClient;

    @BeforeAll
    public static void init() throws MalformedURLException {
        restTemplate = new TestRestTemplate();
        iamServerPort = 8080;
        resourceServerPort = 8082;
        organizationId = OrganizationId.from("it-testing-001");
        projectId = ProjectId.from("spring-method-security");
        userId = UserId.from("user-001");
        clientId = ClientId.from("client-001");
        URL baseUrl = new URL("http://localhost:" + iamServerPort);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(1)
    public void checkIamServerIsAliveTestsIT() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + iamServerPort + "/actuator/info", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void checkResourceServerIsAliveTestsIT() {
        ResponseEntity<SystemInfo> response = restTemplate.getForEntity(
                "http://localhost:" + resourceServerPort + "/services/info", SystemInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    public void getAdminAccessTokens() throws AuthenticationException {
        adminTokens = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertNotNull(adminTokens);
    }

    @Test
    @Order(4)
    public void createOrganizationProjectAndUsers() throws AuthenticationException {
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "IT Testing",
                projectId.getId(),  "Method Security Project",
                clientId.getId(), "top-secret", userId.getId(),  "top-secret", "admin@email.com", Set.of("methodsecurity"));
        SetupOrganizationResponse setupOrganizationResponse = iamServiceManagerClient.setupOrganization(adminTokens.getAccessToken(), setupOrganizationRequest);
        assertNotNull(setupOrganizationResponse);
        IAMAuthorizerClient iamAuthorizerClient = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId);
        userTokens = iamAuthorizerClient.getAccessTokensOAuth2UsernamePassword(userId.getId(), "top-secret", clientId, "top-secret");
        assertNotNull(userTokens);
    }

}
