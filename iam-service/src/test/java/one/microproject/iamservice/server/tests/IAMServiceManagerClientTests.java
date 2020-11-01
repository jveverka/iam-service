package one.microproject.iamservice.server.tests;

import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.client.IAMClientBuilder;
import one.microproject.iamservice.client.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.services.dto.TokenResponse;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static one.microproject.iamservice.core.ModelCommons.ADMIN_ORGANIZATION_SET;
import static one.microproject.iamservice.core.ModelCommons.IAM_SERVICE_CLIENTS_RESOURCE_ACTION_ALL;
import static one.microproject.iamservice.core.ModelCommons.IAM_SERVICE_ORGANIZATIONS_RESOURCE_ACTION_ALL;
import static one.microproject.iamservice.core.ModelCommons.IAM_SERVICE_PROJECTS_RESOURCE_ACTION_ALL;
import static one.microproject.iamservice.core.ModelCommons.IAM_SERVICE_USERS_RESOURCE_ACTION_ALL;
import static one.microproject.iamservice.core.model.utils.ModelUtils.IAM_ADMINS_ORG;
import static one.microproject.iamservice.core.model.utils.ModelUtils.IAM_ADMINS_PROJECT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IAMServiceManagerClientTests {

    private static final Logger LOG = LoggerFactory.getLogger(IAMServiceManagerClientTests.class);

    private static TokenResponse tokenResponse;
    private static IAMClient iamClient;
    private static IAMServiceManagerClient iamServiceManagerClient;

    public static final Set<Permission> NEW_ADMIN_ORGANIZATION_SET = Set.of(
            new Permission("service", "resource", "all"),
            IAM_SERVICE_ORGANIZATIONS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_PROJECTS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_USERS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_CLIENTS_RESOURCE_ACTION_ALL
    );

    @LocalServerPort
    private int port;

    @BeforeAll
    public static void init() {
    }

    @Test
    @Order(1)
    public void createIamClient() throws MalformedURLException, InterruptedException {
        URL baseUrl = new URL("http://localhost:" + port);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        iamClient = IAMClientBuilder.builder()
                .setOrganizationId(IAM_ADMINS_ORG.getId())
                .setProjectId(IAM_ADMINS_PROJECT.getId())
                .withHttpProxy(baseUrl, 10L, TimeUnit.SECONDS)
                .build();
        while(!iamClient.waitForInit(15L, TimeUnit.SECONDS)) {
            LOG.info("waiting for iam-client initialization ...");
        }
    }

    @Test
    @Order(2)
    public void getTokens() throws AuthenticationException {
        tokenResponse = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertNotNull(tokenResponse.getTokenType().equals(TokenType.BEARER.getType()));
        LOG.info("AccessToken : {}", tokenResponse.getAccessToken());
        LOG.info("RefreshToken: {}", tokenResponse.getRefreshToken());
    }

    @Test
    @Order(3)
    public void testValidateTokens() {
        Optional<StandardTokenClaims> claimsSet = iamClient.validate(JWToken.from(tokenResponse.getAccessToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isPresent());
        claimsSet = iamClient.validate(JWToken.from(tokenResponse.getRefreshToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isPresent());
    }

    @Test
    @Order(4)
    public void testValidateTokensForOrganizationAndProjectOK() {
        Optional<StandardTokenClaims> claimsSet = iamClient.validate(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, JWToken.from(tokenResponse.getAccessToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isPresent());
        claimsSet = iamClient.validate(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, JWToken.from(tokenResponse.getRefreshToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isPresent());
    }

    @Test
    @Order(5)
    public void testValidateTokensForOrganizationAndProjectInvalid() {
        Optional<StandardTokenClaims> claimsSet = iamClient.validate(IAM_ADMINS_ORG, ProjectId.from("unknown-project"), JWToken.from(tokenResponse.getAccessToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isEmpty());
        claimsSet = iamClient.validate(OrganizationId.from("unknown-organiation"), IAM_ADMINS_PROJECT, JWToken.from(tokenResponse.getAccessToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isEmpty());
        claimsSet = iamClient.validate(OrganizationId.from("unknown-organiation"), ProjectId.from("unknown-project"), JWToken.from(tokenResponse.getAccessToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isEmpty());
    }

    @Test
    @Order(6)
    public void testValidateTokensForOrganizationAndProjectWithPermissionsOK() {
        boolean result = iamClient.validate(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, ADMIN_ORGANIZATION_SET, JWToken.from(tokenResponse.getAccessToken()));
        assertTrue(result);
        result = iamClient.validate(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, ADMIN_ORGANIZATION_SET, JWToken.from(tokenResponse.getRefreshToken()));
        assertTrue(result);
    }

    @Test
    @Order(7)
    public void testValidateTokensForOrganizationAndProjectWithPermissionsInvalid() {
        boolean result = iamClient.validate(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, NEW_ADMIN_ORGANIZATION_SET, JWToken.from(tokenResponse.getAccessToken()));
        assertFalse(result);
        result = iamClient.validate(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, NEW_ADMIN_ORGANIZATION_SET, JWToken.from(tokenResponse.getRefreshToken()));
        assertFalse(result);
    }

}
