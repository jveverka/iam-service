package one.microproject.iamservice.server.tests;

import one.microproject.iamservice.core.dto.CreateClient;
import one.microproject.iamservice.core.dto.TokenResponseError;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceProjectManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OAuth2ClientCredentialsTests {

    private static IAMServiceManagerClient iamServiceManagerClient;
    private static TokenResponse tokenResponse;
    private static ClientId newClientId = ClientId.from("client-007");
    private static String newClientSecret = "a6s5f4";

    @LocalServerPort
    private int port;

    @Test
    @Order(0)
    void initTests() throws IOException, AuthenticationException {
        URL baseUrl = new URL("http://localhost:" + port);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        assertNotNull(iamServiceManagerClient);
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        tokenResponse = tokenResponseWrapper.getTokenResponse();
        IAMServiceProjectManagerClient iamServiceProject = iamServiceManagerClient.getIAMServiceProject(tokenResponse.getAccessToken(), ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
        ClientProperties properties = new ClientProperties("", false,  false, true, new HashMap<>());
        CreateClient createClient = new CreateClient(newClientId.getId(),  "",  3600L, 3600L, newClientSecret, properties);
        iamServiceProject.createClient(createClient);
    }

    @Test
    @Order(1)
    void getTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2ClientCredentials(newClientId, newClientSecret);
        assertTrue(tokenResponseWrapper.isOk());
        tokenResponse = tokenResponseWrapper.getTokenResponse();
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertEquals(tokenResponse.getTokenType(), TokenType.BEARER.getType());
    }

    @Test
    @Order(2)
    void verifyTokens() throws IOException {
        IntrospectResponse response = iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .tokenIntrospection(tokenResponse.getAccessToken());
        assertNotNull(response);
        assertTrue(response.getActive());
        response = iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .tokenIntrospection(tokenResponse.getRefreshToken());
        assertNotNull(response);
        assertTrue(response.getActive());
    }

    @Test
    @Order(3)
    void getRefreshTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .refreshTokens(tokenResponse.getRefreshToken(), newClientId, newClientSecret);
        assertTrue(tokenResponseWrapper.isOk());
        tokenResponse = tokenResponseWrapper.getTokenResponse();
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertEquals(tokenResponse.getTokenType(), TokenType.BEARER.getType());
    }

    @Test
    @Order(4)
    void verifyRefreshedTokens() throws IOException {
        IntrospectResponse response = iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .tokenIntrospection(tokenResponse.getAccessToken());
        assertNotNull(response);
        assertTrue(response.getActive());
        response = iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .tokenIntrospection(tokenResponse.getRefreshToken());
        assertNotNull(response);
        assertTrue(response.getActive());
    }

    @Test
    @Order(5)
    void revokeTokens() {
        assertDoesNotThrow(() -> iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .revokeToken(tokenResponse.getRefreshToken())
        );
        assertDoesNotThrow(() -> iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .revokeToken(tokenResponse.getAccessToken())
        );
    }

    @Test
    @Order(6)
    void verifyRevokedTokens() throws IOException {
        IntrospectResponse response = iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .tokenIntrospection(tokenResponse.getAccessToken());
        assertNotNull(response);
        assertFalse(response.getActive());
        response = iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .tokenIntrospection(tokenResponse.getRefreshToken());
        assertNotNull(response);
        assertFalse(response.getActive());
    }

    @Test
    @Order(7)
    void testInvalidClientSecretLogin() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2ClientCredentials(newClientId, "invalid-client-secret");
        assertTrue(tokenResponseWrapper.isError());
        TokenResponseError error = tokenResponseWrapper.getTokenResponseError();
        assertNotNull(error);
    }

    @Test
    @Order(8)
    void testInvalidClientIdLogin() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2ClientCredentials(ClientId.from("invalid-client-id"), newClientSecret);
        assertTrue(tokenResponseWrapper.isError());
        TokenResponseError error = tokenResponseWrapper.getTokenResponseError();
        assertNotNull(error);
    }


}
