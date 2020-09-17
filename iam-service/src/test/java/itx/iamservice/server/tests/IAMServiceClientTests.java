package itx.iamservice.server.tests;

import com.nimbusds.jwt.JWTClaimsSet;
import itx.iamservice.client.IAMClient;
import itx.iamservice.client.IAMClientBuilder;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.services.dto.TokenResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenResponseForIAMAdmins;
import static itx.iamservice.core.ModelCommons.ADMIN_ORGANIZATION_SET;
import static itx.iamservice.core.ModelCommons.IAM_SERVICE_CLIENTS_RESOURCE_ACTION_ALL;
import static itx.iamservice.core.ModelCommons.IAM_SERVICE_ORGANIZATIONS_RESOURCE_ACTION_ALL;
import static itx.iamservice.core.ModelCommons.IAM_SERVICE_PROJECTS_RESOURCE_ACTION_ALL;
import static itx.iamservice.core.ModelCommons.IAM_SERVICE_USERS_RESOURCE_ACTION_ALL;
import static itx.iamservice.core.model.utils.ModelUtils.IAM_ADMINS_ORG;
import static itx.iamservice.core.model.utils.ModelUtils.IAM_ADMINS_PROJECT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IAMServiceClientTests {

    private static TokenResponse tokenResponse;
    private static IAMClient iamClient;

    public static final Set<Permission> NEW_ADMIN_ORGANIZATION_SET = Set.of(
            new Permission("service", "resource", "all"),
            IAM_SERVICE_ORGANIZATIONS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_PROJECTS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_USERS_RESOURCE_ACTION_ALL,
            IAM_SERVICE_CLIENTS_RESOURCE_ACTION_ALL
    );

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void init() {
    }

    @Test
    @Order(1)
    public void createIamClient() throws MalformedURLException, URISyntaxException {
        iamClient = IAMClientBuilder.builder()
                .setBaseUrl(new URL("http://localhost:" + port + "/services/authentication"))
                .setOrganizationId(IAM_ADMINS_ORG.getId())
                .setProjectId(IAM_ADMINS_PROJECT.getId())
                .withHttpProxy(10L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(2)
    public void getTokens() {
        tokenResponse = getTokenResponseForIAMAdmins(restTemplate, port);
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertNotNull(tokenResponse.getTokenType().equals(TokenType.BEARER.getType()));
    }

    @Test
    @Order(3)
    public void testValidateTokens() {
        Optional<JWTClaimsSet> claimsSet = iamClient.validate(JWToken.from(tokenResponse.getAccessToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isPresent());
        claimsSet = iamClient.validate(JWToken.from(tokenResponse.getRefreshToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isPresent());
    }

    @Test
    @Order(4)
    public void testValidateTokensForOrganizationAndProjectOK() {
        Optional<JWTClaimsSet> claimsSet = iamClient.validate(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, JWToken.from(tokenResponse.getAccessToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isPresent());
        claimsSet = iamClient.validate(IAM_ADMINS_ORG, IAM_ADMINS_PROJECT, JWToken.from(tokenResponse.getRefreshToken()));
        assertNotNull(claimsSet);
        assertTrue(claimsSet.isPresent());
    }

    @Test
    @Order(5)
    public void testValidateTokensForOrganizationAndProjectInvalid() {
        Optional<JWTClaimsSet> claimsSet = iamClient.validate(IAM_ADMINS_ORG, ProjectId.from("unknown-project"), JWToken.from(tokenResponse.getAccessToken()));
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
