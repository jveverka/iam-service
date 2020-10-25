package itx.iamservice.server.tests;

import itx.iamservice.core.dto.CreateRole;
import itx.iamservice.core.dto.PermissionInfo;
import itx.iamservice.core.dto.RoleInfo;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.ClientInfo;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.serviceclient.IAMServiceManagerClient;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import itx.iamservice.serviceclient.IAMServiceProjectManagerClient;
import itx.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
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
    private static IAMServiceManagerClient iamServiceManagerClient;
    private static IAMServiceProjectManagerClient iamServiceProjectManagerClient;

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void initTest() throws AuthenticationException {
        String baseUrl = "http://localhost:" + port;
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        jwt_admin_token = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret").getAccessToken();
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
        SetupOrganizationResponse setupOrganizationResponse = iamServiceManagerClient.setupOrganization(jwt_admin_token, setupOrganizationRequest);
        assertNotNull(setupOrganizationResponse);
        assertEquals(organizationId.getId(), setupOrganizationResponse.getOrganizationId());
    }

    @Test
    @Order(3)
    public void getTokenOrganizationForAdminUser() throws AuthenticationException {
        jwt_organization_admin_token = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId).getAccessTokensOAuth2UsernamePassword(adminUserId.getId(), adminPassword,
                adminClientId, adminClientSecret).getAccessToken();
        assertNotNull(jwt_organization_admin_token);
        iamServiceProjectManagerClient = iamServiceManagerClient.getIAMServiceProject(jwt_organization_admin_token, organizationId, projectId);
    }

    @Test
    @Order(4)
    public void checkOrganizations() throws IOException {
        Collection<OrganizationInfo> organizations = iamServiceManagerClient.getOrganizations();
        assertNotNull(organizations);
        assertEquals(2, organizations.size());
    }

    @Test
    @Order(5)
    public void checkNewProjectRolesAndPermissions() throws AuthenticationException {
        Set<PermissionInfo> permissions = iamServiceProjectManagerClient.getPermissions();
        assertNotNull(permissions);
        assertEquals(4, permissions.size());
        Collection<RoleInfo> roles = iamServiceProjectManagerClient.getRoles();
        assertEquals(1, roles.size());
    }

    @Test
    @Order(6)
    public void createRoleWithPermissionsTest() throws AuthenticationException {
        Set<PermissionInfo> permissionInfos = new HashSet<>();
        permissionInfos.add(new PermissionInfo(organizationId.getId() + "-" + projectId.getId() , "data", "read"));
        permissionInfos.add(new PermissionInfo(organizationId.getId() + "-" + projectId.getId() , "users", "read"));
        CreateRole createRole = new CreateRole("reader", "Read only user", permissionInfos);
        iamServiceProjectManagerClient.createRole(createRole);
        Set<PermissionInfo> permissions = iamServiceProjectManagerClient.getPermissions();
        assertNotNull(permissions);
        assertEquals(6, permissions.size());
        Collection<RoleInfo> roles = iamServiceProjectManagerClient.getRoles();
        assertEquals(2, roles.size());
    }

    @Test
    @Order(7)
    public void deleteRole() throws AuthenticationException {
        iamServiceProjectManagerClient.deleteRole(RoleId.from("reader"));
        Collection<RoleInfo> roles = iamServiceProjectManagerClient.getRoles();
        assertEquals(1, roles.size());
    }

    @Test
    @Order(8)
    public void deletePermissions() throws AuthenticationException {
        iamServiceProjectManagerClient.deletePermission(PermissionId.from(organizationId.getId() + "-" + projectId.getId() + ".data" + ".read"));
        Set<PermissionInfo> permissions = iamServiceProjectManagerClient.getPermissions();
        assertEquals(5, permissions.size());
        iamServiceProjectManagerClient.deletePermission(PermissionId.from(organizationId.getId() + "-" + projectId.getId() + ".users" + ".read"));
        permissions = iamServiceProjectManagerClient.getPermissions();
        assertEquals(4, permissions.size());
    }

    @Test
    @Order(9)
    public void getProjectInfo() throws IOException {
        ProjectInfo projectInfo = iamServiceProjectManagerClient.getInfo();
        assertNotNull(projectInfo);
        assertEquals(projectId.getId(), projectInfo.getId());
    }

    @Test
    @Order(10)
    public void getUserInfo() throws IOException {
        UserInfo userInfo = iamServiceProjectManagerClient.getUserInfo(adminUserId);
        assertNotNull(userInfo);
        assertEquals(userInfo.getId(), adminUserId.getId());
    }

    @Test
    @Order(11)
    public void getClientInfo() throws IOException {
        ClientInfo clientInfo = iamServiceProjectManagerClient.getClientInfo(adminClientId);
        assertNotNull(clientInfo);
        assertEquals(clientInfo.getId(), adminClientId.getId());
    }

    @Test
    @Order(90)
    public void cleanupProjectInvalidTokenTest() {
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            iamServiceManagerClient.deleteOrganizationRecursively(jwt_organization_admin_token, organizationId);
        });
    }

    @Test
    @Order(91)
    public void cleanupProjectTest() throws AuthenticationException {
        iamServiceManagerClient.deleteOrganizationRecursively(jwt_admin_token, organizationId);
    }

}
