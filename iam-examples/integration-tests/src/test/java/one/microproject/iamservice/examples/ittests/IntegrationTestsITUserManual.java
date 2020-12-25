package one.microproject.iamservice.examples.ittests;

import one.microproject.iamservice.core.dto.CreateClient;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.services.dto.ClientInfo;
import one.microproject.iamservice.core.services.dto.SetupOrganizationRequest;
import one.microproject.iamservice.core.services.dto.SetupOrganizationResponse;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceProjectManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
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
import java.util.Map;
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
    private static IAMServiceProjectManagerClient iamServiceProject;

    private static TokenResponse iamAdminTokens;
    private static TokenResponse projectAdminTokens;

    @BeforeAll
    public static void init() throws MalformedURLException {
        iamServerBaseURL = getIAMServiceURL();
        LOG.info("IAM BASE URL: {}", iamServerBaseURL);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(iamServerBaseURL)
                .withConnectionTimeout(10L, TimeUnit.SECONDS)
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
        iamAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(iamAdminTokens);
        LOG.info("IAM ADMIN access_token  {}", iamAdminTokens.getAccessToken());
    }

    @Test
    @Order(3)
    public void createOrganizationProjectAndAdminUser() throws AuthenticationException {
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "IT Testing",
                projectId.getId(),  "User Manual Example Project",
                projectAdminClientId.getId(), projedtAdminClientSecret, projectAdminUserId.getId(),  projectAdminUserPassword, projectAdminEmail,
                Set.of(), iamServerBaseURL.toString() + "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/redirect",
                UserProperties.getDefault());
        SetupOrganizationResponse setupOrganizationResponse = iamServiceManagerClient.setupOrganization(iamAdminTokens.getAccessToken(), setupOrganizationRequest);
        assertNotNull(setupOrganizationResponse);
    }

    @Test
    @Order(4)
    public void getProjectAdminTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId)
                .getAccessTokensOAuth2UsernamePassword(projectAdminUserId.getId(), projectAdminUserPassword, projectAdminClientId, projedtAdminClientSecret);
        assertTrue(tokenResponseWrapper.isOk());
        projectAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(projectAdminTokens);
        LOG.info("PROJECT ADMIN access_token  {}", projectAdminTokens.getAccessToken());
    }

    @Test
    @Order(5)
    public void createProjectClient() throws IOException, AuthenticationException {
        iamServiceProject = iamServiceManagerClient.getIAMServiceProject(projectAdminTokens.getAccessToken(), organizationId, projectId);
        ClientProperties clientProperties =  new ClientProperties("", true, true, true, Map.of());
        CreateClient createClient = new CreateClient(projectClientId.getId(), "Second Client", 3600L,  3600L, "secret",  clientProperties);
        iamServiceProject.createClient(createClient);

        ClientInfo clientInfo = iamServiceProject.getClientInfo(projectClientId);
        assertNotNull(clientInfo);
        assertEquals(projectClientId.getId(), clientInfo.getId());
    }

    /*
     * Cleanup tests
     */

    @Test
    @Order(80)
    public void deleteProjectClient() throws AuthenticationException {
        iamServiceProject.deleteClient(projectClientId);
    }

}
