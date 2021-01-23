package one.microproject.iamservice.server.tests;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.dto.UserCredentialsChangeRequest;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceUserManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserCredentialsChangeTests {

    private static IAMServiceManagerClient iamServiceManagerClient;
    private static TokenResponse tokenResponse;

    private static final String ADMIN_NAME = "admin";
    private static final String ADMIN_PASSWORD = "secret";

    @LocalServerPort
    private int port;

    @Test
    @Order(0)
    void initTests() throws MalformedURLException {
        URL baseUrl = new URL("http://localhost:" + port);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        assertNotNull(iamServiceManagerClient);
    }

    @Test
    @Order(1)
    void getTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword(ADMIN_NAME, ADMIN_PASSWORD, ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
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
    void changeAdminUserPassword() {
        IAMServiceUserManagerClient iamServiceUserManagerClient = iamServiceManagerClient
                .getIAMServiceUserManagerClient(tokenResponse.getAccessToken(), ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
        UserCredentialsChangeRequest request = new UserCredentialsChangeRequest("new-secret");
        assertDoesNotThrow(() ->
            iamServiceUserManagerClient.changeUserCredentials(ModelUtils.IAM_ADMIN_USER, tokenResponse.getAccessToken(), request)
        );
    }

    @Test
    @Order(3)
    void getTokensAfterPasswordChange() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword(ADMIN_NAME, "new-secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
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
    void changeBackAdminUserPassword() {
        IAMServiceUserManagerClient iamServiceUserManagerClient = iamServiceManagerClient
                .getIAMServiceUserManagerClient(tokenResponse.getAccessToken(), ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
        UserCredentialsChangeRequest request = new UserCredentialsChangeRequest(ADMIN_PASSWORD);
        assertDoesNotThrow(() ->
            iamServiceUserManagerClient.changeUserCredentials(ModelUtils.IAM_ADMIN_USER, tokenResponse.getAccessToken(), request)
        );
    }

    @Test
    @Order(5)
    void getTokensAgain() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword(ADMIN_NAME, ADMIN_PASSWORD, ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        tokenResponse = tokenResponseWrapper.getTokenResponse();
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertEquals(tokenResponse.getTokenType(), TokenType.BEARER.getType());
    }

}
