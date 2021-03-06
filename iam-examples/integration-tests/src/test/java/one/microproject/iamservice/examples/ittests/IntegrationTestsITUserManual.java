package one.microproject.iamservice.examples.ittests;

import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.client.IAMClientBuilder;
import one.microproject.iamservice.core.dto.CreateClient;
import one.microproject.iamservice.core.dto.CreateRole;
import one.microproject.iamservice.core.dto.CreateUser;
import one.microproject.iamservice.core.dto.PermissionInfo;
import one.microproject.iamservice.core.dto.RoleInfo;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.services.dto.ClientInfo;
import one.microproject.iamservice.core.services.dto.OrganizationInfo;
import one.microproject.iamservice.core.services.dto.SetupOrganizationRequest;
import one.microproject.iamservice.core.services.dto.SetupOrganizationResponse;
import one.microproject.iamservice.core.services.dto.UserInfo;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceProjectManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceUserManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static one.microproject.iamservice.examples.ittests.ITTestUtils.getGlobalAdminClientSecret;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.getGlobalAdminPassword;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.getGlobalAdminTokens;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.getIAMServiceURL;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.getIssuerURI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTestsITUserManual {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestsITUserManual.class);

    private static OrganizationId organizationId = OrganizationId.from("test-org-001");
    private static ProjectId projectId = ProjectId.from("project-001");
    private static ClientId projectAdminClientId = ClientId.from("cl-001");
    private static String projectAdminClientSecret = "cl-scrt";
    private static UserId projectAdminUserId = UserId.from("admin");
    private static String projectAdminUserPassword = "some-top-sercret";
    private static String projectAdminEmail = "admin@project-001.com";
    private static ClientId projectClientId = ClientId.from("client-002");

    private static UserId readerUserId = UserId.from("read-user");
    private static UserId writerUserId = UserId.from("writer-user");

    private static URL iamServerBaseURL;
    private static IAMServiceManagerClient iamServiceManagerClient;
    private static IAMServiceProjectManagerClient iamServiceProjectClient;
    private static IAMServiceUserManagerClient iamServiceUserManagerClient;
    private static IAMClient iamClient;

    private static TokenResponse globalAdminTokens;
    private static TokenResponse projectAdminTokens;
    private static TokenResponse readUserTokens;
    private static TokenResponse writeUserTokens;

    private static String globalAdminPassword;
    private static String globalAdminClientSecret;

    @BeforeAll
    public static void init() throws MalformedURLException {
        Security.addProvider(new BouncyCastleProvider());
        iamServerBaseURL = getIAMServiceURL();
        globalAdminPassword = getGlobalAdminPassword();
        globalAdminClientSecret = getGlobalAdminClientSecret();
        LOG.info("IAM BASE URL: {}", iamServerBaseURL);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(iamServerBaseURL)
                .withConnectionTimeout(10L, TimeUnit.SECONDS)
                .build();
        iamClient = IAMClientBuilder.builder()
                .setOrganizationId(organizationId.getId())
                .setProjectId(projectId.getId())
                .withHttpProxy(iamServerBaseURL, 10L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(1)
    void checkIamServerIsAliveBeforeSetup() throws IOException {
        assertTrue(iamServiceManagerClient.isServerAlive());
    }

    @Test
    @Order(2)
    void getIamAdminAccessTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = getGlobalAdminTokens(iamServiceManagerClient, globalAdminPassword, globalAdminClientSecret);
        assertTrue(tokenResponseWrapper.isOk());
        globalAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(globalAdminTokens);
        LOG.info("IAM ADMIN access_token  {}", globalAdminTokens.getAccessToken());
    }

    @Test
    @Order(3)
    void createOrganizationProjectAndAdminUser() throws AuthenticationException, IOException {
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "IT Testing",
                projectId.getId(),  "User Manual Example Project",
                projectAdminClientId.getId(), projectAdminClientSecret, projectAdminUserId.getId(),  projectAdminUserPassword, projectAdminEmail,
                Set.of(), iamServerBaseURL.toString() + "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/redirect",
                UserProperties.getDefault());
        SetupOrganizationResponse setupOrganizationResponse = iamServiceManagerClient.setupOrganization(globalAdminTokens.getAccessToken(), setupOrganizationRequest);
        assertNotNull(setupOrganizationResponse);

        Collection<OrganizationInfo> organizations = iamServiceManagerClient.getOrganizations();
        Optional<OrganizationInfo> organizationInfo = organizations.stream().filter(o->o.getId().equals(organizationId.getId())).findFirst();
        assertTrue(organizationInfo.isPresent());
    }

    @Test
    @Order(4)
    void getProjectAdminTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId)
                .getAccessTokensOAuth2UsernamePassword(projectAdminUserId.getId(), projectAdminUserPassword, projectAdminClientId, projectAdminClientSecret);
        assertTrue(tokenResponseWrapper.isOk());
        projectAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(projectAdminTokens);
        LOG.info("PROJECT ADMIN: {}", projectAdminTokens.getAccessToken());
    }

    @Test
    @Order(5)
    void createProjectClient() throws IOException, AuthenticationException {
        iamServiceProjectClient = iamServiceManagerClient.getIAMServiceProject(projectAdminTokens.getAccessToken(), organizationId, projectId);
        ClientProperties clientProperties =  new ClientProperties("", true, true, true, Map.of());
        CreateClient createClient = new CreateClient(projectClientId.getId(), "Second Client", 3600L,  3600L, "ds65f",  clientProperties);
        iamServiceProjectClient.createClient(createClient);

        ClientInfo clientInfo = iamServiceProjectClient.getClientInfo(projectClientId);
        assertNotNull(clientInfo);
        assertEquals(projectClientId.getId(), clientInfo.getId());
    }

    @Test
    @Order(6)
    void createProjectRoles() throws IOException, AuthenticationException {
        iamServiceProjectClient = iamServiceManagerClient.getIAMServiceProject(projectAdminTokens.getAccessToken(), organizationId, projectId);
        iamServiceUserManagerClient = iamServiceManagerClient.getIAMServiceUserManagerClient(projectAdminTokens.getAccessToken(), organizationId, projectId);
        CreateRole createReaderRole = new CreateRole("reader-role", "", Set.of(
                new PermissionInfo(projectId.getId(), "data-series-all", "read")
        ));
        iamServiceProjectClient.createRole(createReaderRole);
        CreateRole createWriterRole = new CreateRole("writer-role", "", Set.of(
                new PermissionInfo(projectId.getId(), "data-series-all", "all")
        ));
        iamServiceProjectClient.createRole(createWriterRole);
        CreateRole createAdminRole = new CreateRole("admin-role", "", Set.of(
                new PermissionInfo(projectId.getId(), "all", "all")
        ));
        iamServiceProjectClient.createRole(createAdminRole);

        Collection<RoleInfo> roles = iamServiceProjectClient.getRoles();
        Optional<RoleInfo> readerRoleInfo = roles.stream().filter(r->r.getId().equals("reader-role")).findFirst();
        assertTrue(readerRoleInfo.isPresent());
        assertEquals(1L, readerRoleInfo.get().getPermissions().size());

        Optional<RoleInfo> writerRoleInfo = roles.stream().filter(r->r.getId().equals("writer-role")).findFirst();
        assertTrue(writerRoleInfo.isPresent());
        assertEquals(1L, writerRoleInfo.get().getPermissions().size());
    }

    @Test
    @Order(7)
    void testUsersExist() {
        assertThrows(IOException.class, () -> iamServiceUserManagerClient.getUserInfo(readerUserId));
        assertThrows(IOException.class, () -> iamServiceUserManagerClient.getUserInfo(writerUserId));
    }

    @Test
    @Order(8)
    void createReadUser() throws IOException, AuthenticationException {
        CreateUser createReadOnlyUser = new CreateUser(readerUserId.getId(), "", 3600L, 3600L, "", "as87d6a", new UserProperties(Map.of()));
        iamServiceUserManagerClient.createUser(createReadOnlyUser);

        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(readerUserId);
        assertEquals(readerUserId.getId(), userInfo.getId());
    }

    @Test
    @Order(9)
    void createWriteUser() throws IOException, AuthenticationException {
        CreateUser createReadWriteUser = new CreateUser(writerUserId.getId(), "", 3600L, 3600L, "", "6a57dfa", new UserProperties(Map.of()));
        iamServiceUserManagerClient.createUser(createReadWriteUser);

        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(writerUserId);
        assertEquals(writerUserId.getId(), userInfo.getId());
    }

    @Test
    @Order(10)
    void assignRoles() throws IOException, AuthenticationException {
        iamServiceUserManagerClient.addRoleToUser(readerUserId, RoleId.from("reader-role"));
        iamServiceUserManagerClient.addRoleToUser(writerUserId, RoleId.from("writer-role"));
        iamServiceUserManagerClient.addRoleToUser(projectAdminUserId, RoleId.from("admin-role"));

        UserInfo readerUserInfo = iamServiceUserManagerClient.getUserInfo(readerUserId);
        assertEquals(readerUserInfo.getId(), readerUserId.getId());
        assertEquals(1L, readerUserInfo.getRoles().stream().filter(r->r.equals("reader-role")).count());

        UserInfo writerUserInfo = iamServiceUserManagerClient.getUserInfo(writerUserId);
        assertEquals(writerUserInfo.getId(), writerUserId.getId());
        assertEquals(1L, writerUserInfo.getRoles().stream().filter(r->r.equals("writer-role")).count());
    }

    @Test
    @Order(11)
    void getReadUserTokens() throws IOException {
        TokenResponseWrapper readUserWrapper = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId)
                .getAccessTokensOAuth2UsernamePassword(readerUserId.getId(), "as87d6a", projectClientId, "ds65f");
        assertTrue(readUserWrapper.isOk());
        readUserTokens = readUserWrapper.getTokenResponse();
        LOG.info("READ USER: {}", readUserTokens.getAccessToken());
    }

    @Test
    @Order(12)
    void getWriteUserTokens() throws IOException {
        TokenResponseWrapper writeUserWrapper = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId)
                .getAccessTokensOAuth2UsernamePassword(writerUserId.getId(), "6a57dfa", projectClientId, "ds65f");
        assertTrue(writeUserWrapper.isOk());
        writeUserTokens = writeUserWrapper.getTokenResponse();
        LOG.info("WRITE USER: {}", writeUserTokens.getAccessToken());
    }

    @Test
    @Order(13)
    void reloadKeyCache() {
        boolean result = iamClient.updateKeyCache();
        assertTrue(result);
    }

    @Test
    @Order(14)
    void validateProjectAdminTokens() {
        Optional<StandardTokenClaims> adminUserClaims = iamClient.validate(new JWToken(projectAdminTokens.getAccessToken()));
        assertTrue(adminUserClaims.isPresent());
    }

    @Test
    @Order(15)
    void validateReadUserTokens() {
        Optional<StandardTokenClaims> readUserClaims = iamClient.validate(new JWToken(readUserTokens.getAccessToken()));
        assertTrue(readUserClaims.isPresent());
        assertEquals(readerUserId.getId(), readUserClaims.get().getSubject());
        String issuerUri = getIssuerURI(iamServerBaseURL.toString(), organizationId, projectId);
        LOG.info("ISSUER {} {}", issuerUri, readUserClaims.get().getIssuer());
        assertEquals(issuerUri, readUserClaims.get().getIssuer());
    }

    @Test
    @Order(16)
    void validateWriteUserTokens() {
        Optional<StandardTokenClaims> writeUserClaims = iamClient.validate(new JWToken(writeUserTokens.getAccessToken()));
        assertTrue(writeUserClaims.isPresent());
        assertEquals(writerUserId.getId(), writeUserClaims.get().getSubject());
        String issuerUri = getIssuerURI(iamServerBaseURL.toString(), organizationId, projectId);
        LOG.info("ISSUER {} {}", issuerUri, writeUserClaims.get().getIssuer());
        assertEquals(issuerUri, writeUserClaims.get().getIssuer());
    }

    /*
     * Cleanup tests
     */
    @Test
    @Order(80)
    void deleteProjectClient() throws AuthenticationException {
        iamServiceProjectClient.deleteClient(projectClientId);
        assertThrows(IOException.class, () -> iamServiceProjectClient.getClientInfo(projectClientId));
    }

    @Test
    @Order(81)
    void deleteReadUser() throws AuthenticationException {
        iamServiceUserManagerClient.deleteUser(readerUserId);
        assertThrows(IOException.class, () -> iamServiceProjectClient.getUserInfo(readerUserId));
    }

    @Test
    @Order(82)
    void deleteWriteUser() throws AuthenticationException {
        iamServiceUserManagerClient.deleteUser(writerUserId);
        assertThrows(IOException.class, () -> iamServiceProjectClient.getUserInfo(writerUserId));
    }

    @Test
    @Order(83)
    void deleteOrganization() throws AuthenticationException, IOException {
        iamServiceManagerClient.deleteOrganizationRecursively(globalAdminTokens.getAccessToken(), organizationId);

        Collection<OrganizationInfo> organizations = iamServiceManagerClient.getOrganizations();
        Optional<OrganizationInfo> organizationInfo = organizations.stream().filter(o->o.getId().equals(organizationId.getId())).findFirst();
        assertTrue(organizationInfo.isEmpty());

        organizations = iamServiceManagerClient.getOrganizations();
        organizationInfo = organizations.stream().filter(o->o.getId().equals("iam-admins")).findFirst();
        assertTrue(organizationInfo.isPresent());
    }

}
