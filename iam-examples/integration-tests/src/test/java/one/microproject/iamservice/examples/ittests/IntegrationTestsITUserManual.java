package one.microproject.iamservice.examples.ittests;

import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.client.IAMClientBuilder;
import one.microproject.iamservice.core.dto.CreateClient;
import one.microproject.iamservice.core.dto.CreateRole;
import one.microproject.iamservice.core.dto.CreateUser;
import one.microproject.iamservice.core.dto.PermissionInfo;
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

import static one.microproject.iamservice.examples.ittests.ITTestUtils.getIAMAdminTokens;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.getIAMServiceURL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTestsITUserManual {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestsITUserManual.class);

    private static OrganizationId organizationId = OrganizationId.from("test-org-001");
    private static ProjectId projectId = ProjectId.from("project-001");
    private static ClientId projectAdminClientId = ClientId.from("cl-001");
    private static String projedtAdminClientSecret = "cl-scrt";
    private static UserId projectAdminUserId = UserId.from("admin");
    private static String projectAdminUserPassword = "some-top-sercret";
    private static String projectAdminEmail = "admin@project-001.com";
    private static ClientId projectClientId = ClientId.from("client-002");

    private static URL iamServerBaseURL;
    private static IAMServiceManagerClient iamServiceManagerClient;
    private static IAMServiceProjectManagerClient iamServiceProjectClient;
    private static IAMServiceUserManagerClient iamServiceUserManagerClient;
    private static IAMClient iamClient;

    private static TokenResponse globalAdminTokens;
    private static TokenResponse projectAdminTokens;
    private static TokenResponse readUserTokens;
    private static TokenResponse writeUserTokens;

    @BeforeAll
    public static void init() throws MalformedURLException {
        Security.addProvider(new BouncyCastleProvider());
        iamServerBaseURL = getIAMServiceURL();
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
    public void checkIamServerIsAliveBeforeSetup() throws IOException {
        assertTrue(iamServiceManagerClient.isServerAlive());
    }

    @Test
    @Order(2)
    public void getIamAdminAccessTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = getIAMAdminTokens(iamServiceManagerClient);
        assertTrue(tokenResponseWrapper.isOk());
        globalAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(globalAdminTokens);
        LOG.info("IAM ADMIN access_token  {}", globalAdminTokens.getAccessToken());
    }

    @Test
    @Order(3)
    public void createOrganizationProjectAndAdminUser() throws AuthenticationException, IOException {
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "IT Testing",
                projectId.getId(),  "User Manual Example Project",
                projectAdminClientId.getId(), projedtAdminClientSecret, projectAdminUserId.getId(),  projectAdminUserPassword, projectAdminEmail,
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
    public void getProjectAdminTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId)
                .getAccessTokensOAuth2UsernamePassword(projectAdminUserId.getId(), projectAdminUserPassword, projectAdminClientId, projedtAdminClientSecret);
        assertTrue(tokenResponseWrapper.isOk());
        projectAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(projectAdminTokens);
        LOG.info("PROJECT ADMIN: {}", projectAdminTokens.getAccessToken());
    }

    @Test
    @Order(5)
    public void createProjectClient() throws IOException, AuthenticationException {
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
    public void createProjectRoles() throws IOException, AuthenticationException {
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
    }

    @Test
    @Order(7)
    public void createReadUser() throws IOException, AuthenticationException {
        CreateUser createReadOnlyUser = new CreateUser("read-user", "", 3600L, 3600L, "", "as87d6a", new UserProperties(Map.of()));
        iamServiceUserManagerClient.createUser(createReadOnlyUser);
    }

    @Test
    @Order(8)
    public void createWriteUser() throws IOException, AuthenticationException {
        CreateUser createReadWriteUser = new CreateUser("write-user", "", 3600L, 3600L, "", "6a57dfa", new UserProperties(Map.of()));
        iamServiceUserManagerClient.createUser(createReadWriteUser);
    }

    @Test
    @Order(9)
    public void assignRoles() throws IOException, AuthenticationException {
        iamServiceUserManagerClient.addRoleToUser(UserId.from("read-user"), RoleId.from("reader-role"));
        iamServiceUserManagerClient.addRoleToUser(UserId.from("write-user"), RoleId.from("writer-role"));
        iamServiceUserManagerClient.addRoleToUser(UserId.from("admin"), RoleId.from("admin-role"));
    }

    @Test
    @Order(10)
    public void getReadUserTokens() throws IOException {
        TokenResponseWrapper readUserWrapper = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId)
                .getAccessTokensOAuth2UsernamePassword("read-user", "as87d6a", projectClientId, "ds65f");
        assertTrue(readUserWrapper.isOk());
        readUserTokens = readUserWrapper.getTokenResponse();
        LOG.info("READ USER: {}", readUserTokens.getAccessToken());
    }

    @Test
    @Order(11)
    public void getWriteUserTokens() throws IOException {
        TokenResponseWrapper writeUserWrapper = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId)
                .getAccessTokensOAuth2UsernamePassword("write-user", "6a57dfa", projectClientId, "ds65f");
        assertTrue(writeUserWrapper.isOk());
        writeUserTokens = writeUserWrapper.getTokenResponse();
        LOG.info("WRITE USER: {}", writeUserTokens.getAccessToken());
    }

    @Test
    @Order(12)
    public void reloadKeyCache() {
        boolean result = iamClient.updateKeyCache();
        assertTrue(result);
    }

    @Test
    @Order(13)
    public void validateProjectAdminTokens() {
        Optional<StandardTokenClaims> adminUserClaims = iamClient.validate(new JWToken(projectAdminTokens.getAccessToken()));
        assertTrue(adminUserClaims.isPresent());
    }

    @Test
    @Order(14)
    public void validateReadUserTokens() {
        Optional<StandardTokenClaims> readUserClaims = iamClient.validate(new JWToken(readUserTokens.getAccessToken()));
        assertTrue(readUserClaims.isPresent());
    }

    @Test
    @Order(15)
    public void validateWriteUserTokens() {
        Optional<StandardTokenClaims> writeUserClaims = iamClient.validate(new JWToken(writeUserTokens.getAccessToken()));
        assertTrue(writeUserClaims.isPresent());
    }

    /*
     * Cleanup tests
     */
    @Test
    @Order(80)
    public void deleteProjectClient() throws AuthenticationException {
        iamServiceProjectClient.deleteClient(projectClientId);
    }

    @Test
    @Order(81)
    public void deleteReadUser() throws AuthenticationException {
        iamServiceUserManagerClient.deleteUser(UserId.from("read-user"));
    }

    @Test
    @Order(82)
    public void deleteWriteUser() throws AuthenticationException {
        iamServiceUserManagerClient.deleteUser(UserId.from("write-user"));
    }

    @Test
    @Order(83)
    public void deleteOrganization() throws AuthenticationException, IOException {
        iamServiceManagerClient.deleteOrganizationRecursively(globalAdminTokens.getAccessToken(), organizationId);
        Collection<OrganizationInfo> organizations = iamServiceManagerClient.getOrganizations();
        Optional<OrganizationInfo> organizationInfo = organizations.stream().filter(o->o.getId().equals(organizationId.getId())).findFirst();
        assertTrue(organizationInfo.isEmpty());
        organizations = iamServiceManagerClient.getOrganizations();
        organizationInfo = organizations.stream().filter(o->o.getId().equals("iam-admins")).findFirst();
        assertTrue(organizationInfo.isPresent());
    }

}
