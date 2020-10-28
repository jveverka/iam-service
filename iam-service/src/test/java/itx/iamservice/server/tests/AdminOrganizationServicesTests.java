package itx.iamservice.server.tests;

import itx.iamservice.core.dto.CreateRole;
import itx.iamservice.core.dto.CreateUser;
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
import itx.iamservice.serviceclient.IAMServiceUserManagerClient;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static UserId newUserId = UserId.from("user-id-002");
    private static RoleId newRoleId = RoleId.from("reader-role");

    private static String jwt_organization_admin_token;
    private static IAMServiceManagerClient iamServiceManagerClient;
    private static IAMServiceProjectManagerClient iamServiceProjectManagerClient;
    private static IAMServiceUserManagerClient iamServiceUserManagerClient;

    @LocalServerPort
    private int port;

    @Test
    @Order(101)
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
    @Order(102)
    public void createNewOrganizationWithAdminUser() throws AuthenticationException {
        Set<String> projectAudience = new HashSet<>();
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "My Organization 001",
                projectId.getId(), "My Project 001",
                adminClientId.getId(), adminClientSecret,
                adminUserId.getId(), adminPassword, "user1@email.com", projectAudience);
        SetupOrganizationResponse setupOrganizationResponse = iamServiceManagerClient.setupOrganization(jwt_admin_token, setupOrganizationRequest);
        assertNotNull(setupOrganizationResponse);
        assertEquals(organizationId.getId(), setupOrganizationResponse.getOrganizationId());
    }

    @Test
    @Order(103)
    public void getTokenOrganizationForAdminUser() throws AuthenticationException {
        jwt_organization_admin_token = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId).getAccessTokensOAuth2UsernamePassword(adminUserId.getId(), adminPassword,
                adminClientId, adminClientSecret).getAccessToken();
        assertNotNull(jwt_organization_admin_token);
        iamServiceProjectManagerClient = iamServiceManagerClient.getIAMServiceProject(jwt_organization_admin_token, organizationId, projectId);
        iamServiceUserManagerClient = iamServiceManagerClient.getIAMServiceUserManagerClient(jwt_organization_admin_token, organizationId, projectId);
    }

    @Test
    @Order(104)
    public void checkOrganizations() throws IOException {
        Collection<OrganizationInfo> organizations = iamServiceManagerClient.getOrganizations();
        assertNotNull(organizations);
        assertEquals(2, organizations.size());
    }

    @Test
    @Order(105)
    public void checkNewProjectRolesAndPermissions() throws AuthenticationException {
        Set<PermissionInfo> permissions = iamServiceProjectManagerClient.getPermissions();
        assertNotNull(permissions);
        assertEquals(4, permissions.size());
        Collection<RoleInfo> roles = iamServiceProjectManagerClient.getRoles();
        assertEquals(1, roles.size());
    }

    @Test
    @Order(106)
    public void createRoleWithPermissionsTest() throws AuthenticationException {
        Set<PermissionInfo> permissionInfos = new HashSet<>();
        permissionInfos.add(new PermissionInfo(organizationId.getId() + "-" + projectId.getId() , "data", "read"));
        permissionInfos.add(new PermissionInfo(organizationId.getId() + "-" + projectId.getId() , "users", "read"));
        CreateRole createRole = new CreateRole(newRoleId.getId(), "Read only user", permissionInfos);
        iamServiceProjectManagerClient.createRole(createRole);
        Set<PermissionInfo> permissions = iamServiceProjectManagerClient.getPermissions();
        assertNotNull(permissions);
        assertEquals(6, permissions.size());
        Collection<RoleInfo> roles = iamServiceProjectManagerClient.getRoles();
        assertEquals(2, roles.size());
    }

    @Test
    @Order(201)
    public void createNewUser() throws AuthenticationException, IOException {
        CreateUser createUser = new CreateUser(newUserId.getId(),  "User 2", 3600L, 3600L, "user@email.com");
        iamServiceUserManagerClient.createUser(createUser);
        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(newUserId);
        assertEquals(userInfo.getId(), newUserId.getId());
    }

    @Test
    @Order(202)
    public void addRoleToUser() throws AuthenticationException, IOException {
        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(newUserId);
        Optional<String> roleIdOptional = userInfo.getRoles().stream().filter(r -> r.equals(newRoleId.getId())).findFirst();
        assertTrue(roleIdOptional.isEmpty());
        ProjectInfo projectInfo = iamServiceProjectManagerClient.getInfo();
        Optional<String> userIdOptional = projectInfo.getUsers().stream().filter(u -> u.equals(newUserId.getId())).findFirst();
        assertTrue(userIdOptional.isPresent());
        iamServiceUserManagerClient.addRoleToUser(newUserId, newRoleId);
        userInfo = iamServiceUserManagerClient.getUserInfo(newUserId);
        roleIdOptional = userInfo.getRoles().stream().filter(r -> r.equals(newRoleId.getId())).findFirst();
        assertTrue(roleIdOptional.isPresent());
    }

    @Test
    @Order(203)
    public void removeRoleFromUser() throws AuthenticationException, IOException {
        iamServiceUserManagerClient.removeRoleFromUser(newUserId, newRoleId);
        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(newUserId);
        Optional<String> roleIdOptional = userInfo.getRoles().stream().filter(r -> r.equals(newRoleId.getId())).findFirst();
        assertTrue(roleIdOptional.isEmpty());
    }

    @Test
    @Order(204)
    public void deleteUser() throws AuthenticationException, IOException {
        iamServiceUserManagerClient.deleteUser(newUserId);
        ProjectInfo projectInfo = iamServiceProjectManagerClient.getInfo();
        Optional<String> userIdOptional = projectInfo.getUsers().stream().filter(u -> u.equals(newUserId.getId())).findFirst();
        assertTrue(userIdOptional.isEmpty());
    }

    @Test
    @Order(807)
    public void deleteRole() throws AuthenticationException {
        iamServiceProjectManagerClient.deleteRole(newRoleId);
        Collection<RoleInfo> roles = iamServiceProjectManagerClient.getRoles();
        assertEquals(1, roles.size());
    }

    @Test
    @Order(808)
    public void deletePermissions() throws AuthenticationException {
        iamServiceProjectManagerClient.deletePermission(PermissionId.from(organizationId.getId() + "-" + projectId.getId() + ".data" + ".read"));
        Set<PermissionInfo> permissions = iamServiceProjectManagerClient.getPermissions();
        assertEquals(5, permissions.size());
        iamServiceProjectManagerClient.deletePermission(PermissionId.from(organizationId.getId() + "-" + projectId.getId() + ".users" + ".read"));
        permissions = iamServiceProjectManagerClient.getPermissions();
        assertEquals(4, permissions.size());
    }

    @Test
    @Order(809)
    public void getProjectInfo() throws IOException {
        ProjectInfo projectInfo = iamServiceProjectManagerClient.getInfo();
        assertNotNull(projectInfo);
        assertEquals(projectId.getId(), projectInfo.getId());
    }

    @Test
    @Order(810)
    public void getUserInfo() throws IOException {
        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(adminUserId);
        assertNotNull(userInfo);
        assertEquals(userInfo.getId(), adminUserId.getId());
    }

    @Test
    @Order(811)
    public void getClientInfo() throws IOException {
        ClientInfo clientInfo = iamServiceProjectManagerClient.getClientInfo(adminClientId);
        assertNotNull(clientInfo);
        assertEquals(clientInfo.getId(), adminClientId.getId());
    }

    @Test
    @Order(990)
    public void cleanupProjectInvalidTokenTest() {
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            iamServiceManagerClient.deleteOrganizationRecursively(jwt_organization_admin_token, organizationId);
        });
    }

    @Test
    @Order(991)
    public void cleanupProjectTest() throws AuthenticationException {
        iamServiceManagerClient.deleteOrganizationRecursively(jwt_admin_token, organizationId);
    }

}
