package one.microproject.iamservice.examples.ittests;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.services.dto.SetupOrganizationRequest;
import one.microproject.iamservice.core.services.dto.SetupOrganizationResponse;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static one.microproject.iamservice.examples.ittests.ITTestUtils.getGlobalAdminTokens;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.getIAMServiceURL;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.organizationId;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.projectId;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.clientId;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.appAdminUserId;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTestsITSetup {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestsITSetup.class);

    private static URL iamServerBaseURL;
    private static TokenResponse iamAdminTokens;
    private static IAMServiceManagerClient iamServiceManagerClient;

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
        TokenResponseWrapper tokenResponseWrapper = getGlobalAdminTokens(iamServiceManagerClient);
        assertTrue(tokenResponseWrapper.isOk());
        iamAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(iamAdminTokens);
        LOG.info("IAM ADMIN access_token  {}", iamAdminTokens.getAccessToken());
    }

    @Test
    @Order(3)
    public void createOrganizationProjectAndAdminUser() throws AuthenticationException {
        SetupOrganizationRequest setupOrganizationRequest = new SetupOrganizationRequest(organizationId.getId(), "IT Testing",
                projectId.getId(),  "Method Security Project",
                clientId.getId(), "top-secret", appAdminUserId.getId(),  "secret", "admin@email.com",
                Set.of("methodsecurity"), iamServerBaseURL.toString() + "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId() + "/redirect",
                UserProperties.getDefault());
        SetupOrganizationResponse setupOrganizationResponse = iamServiceManagerClient.setupOrganization(iamAdminTokens.getAccessToken(), setupOrganizationRequest);
        assertNotNull(setupOrganizationResponse);
    }

}
