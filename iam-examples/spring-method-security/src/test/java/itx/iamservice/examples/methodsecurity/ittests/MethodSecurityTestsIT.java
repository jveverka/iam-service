package itx.iamservice.examples.methodsecurity.ittests;


import itx.iamservice.core.dto.HealthCheckResponse;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.CreateClientRequest;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.CreatePermissionRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateUserRequest;
import itx.iamservice.core.services.dto.SetUserNamePasswordCredentialsRequest;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.examples.methodsecurity.dto.SystemInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createClientOnTheProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createNewOrganization;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createPermissionOnProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.createUserOnProject;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getHealthCheckResponse;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenResponseForUserNameAndPassword;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.setUsernamePasswordCredentialsForProjectAndUser;
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

    @BeforeAll
    public static void init() {
        restTemplate = new TestRestTemplate();
        iamServerPort = 8080;
        resourceServerPort = 8082;
    }

    @Test
    @Order(1)
    public void checkIamServerIsAliveTestsIT() {
        HealthCheckResponse healthCheckResponse = getHealthCheckResponse(restTemplate, iamServerPort);
        assertNotNull(healthCheckResponse);
        adminTokens = getTokenResponseForUserNameAndPassword(restTemplate, iamServerPort);
        assertNotNull(adminTokens);

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
    public void createOrganizationProjectAndUsers() {
        CreateOrganizationRequest request = new CreateOrganizationRequest(OrganizationId.from("it-testing-001"), "IT Testing");
        organizationId = createNewOrganization(adminTokens.getAccessToken(), restTemplate, iamServerPort, request);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest(ProjectId.from("spring-method-security"), "Method Security Project", Set.of("methodsecurity"));
        projectId = createProject(adminTokens.getAccessToken(), restTemplate, iamServerPort, organizationId, createProjectRequest);
        createPermissionOnProject(adminTokens.getAccessToken(), restTemplate, iamServerPort, organizationId, projectId, new CreatePermissionRequest("methodsecurity", "data", "read"));
        createPermissionOnProject(adminTokens.getAccessToken(), restTemplate, iamServerPort, organizationId, projectId, new CreatePermissionRequest("methodsecurity", "data", "modify"));
        CreateUserRequest createUserRequest = new CreateUserRequest(UserId.from("user-001"), "name",  3600*1000L, 3600*1000L);
        userId = createUserOnProject(adminTokens.getAccessToken(), restTemplate, iamServerPort, organizationId, projectId, createUserRequest);
        SetUserNamePasswordCredentialsRequest setUserNamePasswordCredentialsRequest = new SetUserNamePasswordCredentialsRequest(userId.getId(), "top-secret");
        setUsernamePasswordCredentialsForProjectAndUser(adminTokens.getAccessToken(), restTemplate, iamServerPort, organizationId, projectId, userId, setUserNamePasswordCredentialsRequest);
        CreateClientRequest createClientRequest = new CreateClientRequest(ClientId.from("client-001"), "name", 3600*1000L, 3600*1000L, "top-secret");
        ClientCredentials clientCredentials = createClientOnTheProject(adminTokens.getAccessToken(), restTemplate, iamServerPort, organizationId, projectId, createClientRequest);
        userTokens = getTokenResponseForUserNameAndPassword(restTemplate, iamServerPort, userId.getId(), "top-secret", clientCredentials.getId(), clientCredentials.getSecret(), organizationId, projectId);
        assertNotNull(userTokens);
    }

}
