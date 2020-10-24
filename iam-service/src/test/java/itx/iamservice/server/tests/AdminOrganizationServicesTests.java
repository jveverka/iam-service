package itx.iamservice.server.tests;

import itx.iamservice.core.dto.PermissionInfo;
import itx.iamservice.core.dto.RoleInfo;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import itx.iamservice.serviceclient.IAMServiceClient;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import itx.iamservice.serviceclient.IAMServiceProject;
import itx.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminOrganizationServicesTests {

    private static final Logger LOG = LoggerFactory.getLogger(AdminOrganizationServicesTests.class);

    private static String jwt_admin_token;
    private static OrganizationId organizationId = OrganizationId.from("my-org-001");
    private static ProjectId projectId = ProjectId.from("project-001");
    private static ClientId adminClientId = ClientId.from("acl-001");
    private static String adminClientSecret = "acl-secret";
    private static UserId adminUserId = UserId.from("admin");
    private static String adminPassword = "secret";
    private static String jwt_organization_admin_token;
    private static IAMServiceClient iamServiceClient;
    private static IAMServiceProject iamServiceProject;

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void initTest() throws AuthenticationException {
        String baseUrl = "http://localhost:" + port;
        iamServiceClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        jwt_admin_token = iamServiceClient.getAccessTokensForIAMAdmin("secret", "top-secret").getAccessToken();
        LOG.info("JSW  access_token: {}", jwt_admin_token);
    }

    @Test
    @Order(2)
    public void createNewOrganizationWithAdminUser() throws AuthenticationException {
        Set<String> projectAudience = new HashSet<>();
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "My Organization 001",
                projectId.getId(), "My Project 001",
                adminClientId.getId(), adminClientSecret,
                adminUserId.getId(), adminPassword, projectAudience);
        SetupOrganizationResponse setupOrganizationResponse = iamServiceClient.setupOrganization(jwt_admin_token, setupOrganizationRequest);
        assertNotNull(setupOrganizationResponse);
        assertEquals(organizationId.getId(), setupOrganizationResponse.getOrganizationId());
    }

    @Test
    @Order(3)
    public void getTokenOrganizationForAdminUser() throws AuthenticationException {
        jwt_organization_admin_token = iamServiceClient.getAccessTokens(organizationId, projectId, adminUserId.getId(), adminPassword,
                adminClientId, adminClientSecret).getAccessToken();
        assertNotNull(jwt_organization_admin_token);
        iamServiceProject = iamServiceClient.getIAMServiceProject(jwt_organization_admin_token, organizationId, projectId);
    }

    @Test
    @Order(4)
    public void checkNewProjectRolesAndPermissions() throws AuthenticationException {
        Set<PermissionInfo> permissions = iamServiceProject.getPermissions();
        assertNotNull(permissions);
        assertEquals(4, permissions.size());
        Collection<RoleInfo> roles = iamServiceProject.getRoles();
        assertEquals(1, roles.size());
    }

    @Test
    @Order(90)
    public void cleanupProjectInvalidTokenTest() {
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            iamServiceClient.deleteOrganizationRecursively(jwt_organization_admin_token, organizationId);
        });
    }

    @Test
    @Order(91)
    public void cleanupProjectTest() throws AuthenticationException {
        iamServiceClient.deleteOrganizationRecursively(jwt_admin_token, organizationId);
    }

}
