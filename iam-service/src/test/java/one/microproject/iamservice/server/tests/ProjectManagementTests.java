package one.microproject.iamservice.server.tests;

import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.client.IAMClientBuilder;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.dto.CreateClient;
import one.microproject.iamservice.core.dto.CreateRole;
import one.microproject.iamservice.core.dto.CreateUser;
import one.microproject.iamservice.core.dto.PermissionInfo;
import one.microproject.iamservice.core.dto.RoleInfo;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PermissionId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.services.dto.ClientInfo;
import one.microproject.iamservice.core.services.dto.OrganizationInfo;
import one.microproject.iamservice.core.services.dto.ProjectInfo;
import one.microproject.iamservice.core.services.dto.SetupOrganizationRequest;
import one.microproject.iamservice.core.services.dto.SetupOrganizationResponse;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.services.dto.UserInfo;
import one.microproject.iamservice.serviceclient.IAMAuthorizerClient;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceProjectManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceUserManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectManagementTests {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectManagementTests.class);

    private static URL baseUrl;
    private static String jwt_admin_token;
    private static OrganizationId organizationId = OrganizationId.from("my-org-001");
    private static ProjectId projectId = ProjectId.from("project-001");
    private static ClientId adminClientId = ClientId.from("acl-001");
    private static String adminClientSecret = "acl-secret";
    private static UserId adminUserId = UserId.from("admin");
    private static String adminPassword = "secret";

    private static UserId newUserId = UserId.from("user-id-002");
    private static ClientId newClientId = ClientId.from("user-id-002-client");
    private static RoleId newRoleId = RoleId.from("reader-role");
    private static ClientProperties newClientProperties = ClientProperties.from(ModelUtils.getRedirectURL(organizationId, projectId));

    private static String jwt_organization_admin_token;
    private static IAMClient iamClient;
    private static IAMServiceManagerClient iamServiceManagerClient;
    private static IAMServiceProjectManagerClient iamServiceProjectManagerClient;
    private static IAMServiceUserManagerClient iamServiceUserManagerClient;

    @LocalServerPort
    private int port;

    @Test
    @Order(101)
    void initTest() throws IOException {
        baseUrl = new URL("http://localhost:" + port);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        TokenResponse tokenResponse = tokenResponseWrapper.getTokenResponse();
        jwt_admin_token = tokenResponse.getAccessToken();
        LOG.info("JSW  access_token: {}", jwt_admin_token);
    }

    @Test
    @Order(102)
    void createNewOrganizationWithAdminUser() throws AuthenticationException, InterruptedException, IOException {
        Set<String> projectAudience = new HashSet<>();
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "My Organization 001",
                projectId.getId(), "My Project 001",
                adminClientId.getId(), adminClientSecret,
                adminUserId.getId(), adminPassword, "user1@email.com", projectAudience,
                newClientProperties.getRedirectURL(), UserProperties.getDefault());
        SetupOrganizationResponse setupOrganizationResponse = iamServiceManagerClient.setupOrganization(jwt_admin_token, setupOrganizationRequest);
        assertNotNull(setupOrganizationResponse);
        assertEquals(organizationId.getId(), setupOrganizationResponse.getOrganizationId());

        Collection<OrganizationInfo> organizations = iamServiceManagerClient.getOrganizations();
        Optional<OrganizationInfo> organizationOptional = organizations.stream().filter(o -> o.getId().equals(organizationId.getId())).findFirst();
        assertTrue(organizationOptional.isPresent());

        iamClient = IAMClientBuilder.builder()
                .setOrganizationId(organizationId.getId())
                .setProjectId(projectId.getId())
                .withHttpProxy(baseUrl, 5L, TimeUnit.SECONDS)
                .build();
        iamClient.waitForInit(10L,  TimeUnit.SECONDS);
    }

    @Test
    @Order(103)
    void getTokenOrganizationForAdminUser() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAuthorizerClient(organizationId, projectId)
                .getAccessTokensOAuth2UsernamePassword(adminUserId.getId(), adminPassword,
                adminClientId, adminClientSecret);
        assertTrue(tokenResponseWrapper.isOk());
        TokenResponse tokenResponse = tokenResponseWrapper.getTokenResponse();
        jwt_organization_admin_token = tokenResponse.getAccessToken();
        iamServiceProjectManagerClient = iamServiceManagerClient.getIAMServiceProject(jwt_organization_admin_token, organizationId, projectId);
        iamServiceUserManagerClient = iamServiceManagerClient.getIAMServiceUserManagerClient(jwt_organization_admin_token, organizationId, projectId);
    }

    @Test
    @Order(104)
    void checkOrganizations() throws IOException {
        Collection<OrganizationInfo> organizations = iamServiceManagerClient.getOrganizations();
        assertNotNull(organizations);
        assertEquals(2, organizations.size());
    }

    @Test
    @Order(105)
    void checkNewProjectRolesAndPermissions() throws AuthenticationException {
        Set<PermissionInfo> permissions = iamServiceProjectManagerClient.getPermissions();
        assertNotNull(permissions);
        assertEquals(4, permissions.size());
        Collection<RoleInfo> roles = iamServiceProjectManagerClient.getRoles();
        assertEquals(1, roles.size());
    }

    @Test
    @Order(106)
    void createRoleWithPermissionsTest() throws AuthenticationException {
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
    void createNewUser() throws AuthenticationException, IOException {
        CreateUser createUser = new CreateUser(newUserId.getId(),  "User 2", 3600L, 3600L,
                "user@email.com", "s3cr3t", UserProperties.getDefault());
        iamServiceUserManagerClient.createUser(createUser);
        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(newUserId);
        assertEquals(userInfo.getId(), newUserId.getId());
    }

    @Test
    @Order(202)
    void createNewClient() throws AuthenticationException, IOException {
        CreateClient createClient = new CreateClient(newClientId.getId(), "", 3600L, 3600L, "top-s3cre3t", newClientProperties);
        iamServiceProjectManagerClient.createClient(createClient);
        ClientInfo clientInfo = iamServiceProjectManagerClient.getClientInfo(newClientId);
        assertEquals(clientInfo.getId(), newClientId.getId());
    }

    @Test
    @Order(202)
    void addRoleToUser() throws AuthenticationException, IOException {
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
    void getTokensForUser() throws IOException {
        IAMAuthorizerClient iamAuthorizerClient = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId);
        TokenResponseWrapper tokenResponseWrapper = iamAuthorizerClient
                .getAccessTokensOAuth2UsernamePassword(newUserId.getId(), "s3cr3t", newClientId, "top-s3cre3t");
        assertTrue(tokenResponseWrapper.isOk());
        TokenResponse newUserTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(newUserTokens);
        assertNotNull(newUserTokens.getAccessToken());
        assertNotNull(newUserTokens.getRefreshToken());
        assertNotNull(newUserTokens.getIdToken());
        iamClient.updateKeyCache();
        Optional<StandardTokenClaims> tokenClaims = iamClient.validate(JWToken.from(newUserTokens.getAccessToken()));
        assertTrue(tokenClaims.isPresent());
    }

    @Test
    @Order(204)
    void removeRoleFromUser() throws AuthenticationException, IOException {
        iamServiceUserManagerClient.removeRoleFromUser(newUserId, newRoleId);
        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(newUserId);
        Optional<String> roleIdOptional = userInfo.getRoles().stream().filter(r -> r.equals(newRoleId.getId())).findFirst();
        assertTrue(roleIdOptional.isEmpty());
    }

    @Test
    @Order(205)
    void deleteNewUser() throws AuthenticationException, IOException {
        iamServiceUserManagerClient.deleteUser(newUserId);
        ProjectInfo projectInfo = iamServiceProjectManagerClient.getInfo();
        Optional<String> userIdOptional = projectInfo.getUsers().stream().filter(u -> u.equals(newUserId.getId())).findFirst();
        assertTrue(userIdOptional.isEmpty());
    }

    @Test
    @Order(206)
    void deleteNewClient() throws AuthenticationException, IOException {
        Optional<String> optional = iamServiceProjectManagerClient.getInfo().getClients().stream().filter(c -> c.equals(newClientId.getId())).findFirst();
        assertTrue(optional.isPresent());
        iamServiceProjectManagerClient.deleteClient(newClientId);
        optional = iamServiceProjectManagerClient.getInfo().getClients().stream().filter(c -> c.equals(newClientId.getId())).findFirst();
        assertTrue(optional.isEmpty());
    }

    @Test
    @Order(207)
    void setAudienceTest() throws AuthenticationException, IOException {
        Set<String> audience1 = Set.of("a1", "a2");
        Set<String> audience2 = Set.of("a5", "a6");
        iamServiceProjectManagerClient.setAudience(audience1);
        ProjectInfo projectInfo = iamServiceProjectManagerClient.getInfo();
        assertEquals(audience1, projectInfo.getAudience());
        iamServiceProjectManagerClient.setAudience(audience2);
        projectInfo = iamServiceProjectManagerClient.getInfo();
        assertEquals(audience2, projectInfo.getAudience());
    }

    @Test
    @Order(807)
    void deleteRole() throws AuthenticationException {
        iamServiceProjectManagerClient.deleteRole(newRoleId);
        Collection<RoleInfo> roles = iamServiceProjectManagerClient.getRoles();
        assertEquals(1, roles.size());
    }

    @Test
    @Order(808)
    void deletePermissions() throws AuthenticationException {
        iamServiceProjectManagerClient.deletePermission(PermissionId.from(organizationId.getId() + "-" + projectId.getId() + ".data" + ".read"));
        Set<PermissionInfo> permissions = iamServiceProjectManagerClient.getPermissions();
        assertEquals(5, permissions.size());
        iamServiceProjectManagerClient.deletePermission(PermissionId.from(organizationId.getId() + "-" + projectId.getId() + ".users" + ".read"));
        permissions = iamServiceProjectManagerClient.getPermissions();
        assertEquals(4, permissions.size());
    }

    @Test
    @Order(809)
    void getProjectInfo() throws IOException {
        ProjectInfo projectInfo = iamServiceProjectManagerClient.getInfo();
        assertNotNull(projectInfo);
        assertEquals(projectId.getId(), projectInfo.getId());
    }

    @Test
    @Order(810)
    void getUserInfo() throws IOException {
        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(adminUserId);
        assertNotNull(userInfo);
        assertEquals(userInfo.getId(), adminUserId.getId());
    }

    @Test
    @Order(811)
    void getClientInfo() throws IOException {
        ClientInfo clientInfo = iamServiceProjectManagerClient.getClientInfo(adminClientId);
        assertNotNull(clientInfo);
        assertEquals(clientInfo.getId(), adminClientId.getId());
    }

    @Test
    @Order(990)
    void cleanupProjectInvalidTokenTest() {
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            iamServiceManagerClient.deleteOrganizationRecursively(jwt_organization_admin_token, organizationId);
        });
    }

    @Test
    @Order(991)
    void cleanupProjectTest() {
        assertDoesNotThrow(() ->
            iamServiceManagerClient.deleteOrganizationRecursively(jwt_admin_token, organizationId)
        );
    }

}
