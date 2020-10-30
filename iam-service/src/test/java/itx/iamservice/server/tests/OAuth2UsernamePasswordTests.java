package itx.iamservice.server.tests;

import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.core.services.dto.UserInfoResponse;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import itx.iamservice.serviceclient.IAMServiceManagerClient;
import itx.iamservice.serviceclient.impl.AuthenticationException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuth2UsernamePasswordTests {

    private static IAMServiceManagerClient iamServiceManagerClient;
    private static TokenResponse tokenResponse;

    @LocalServerPort
    private int port;

    @Test
    @Order(0)
    public void initTests() throws MalformedURLException {
        URL baseUrl = new URL("http://localhost:" + port);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(1)
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
    }

    @Test
    @Order(2)
    public void verifyTokens() throws IOException {
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
    public void testUserInfo() throws IOException {
        UserInfoResponse response = iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .getUserInfo(tokenResponse.getAccessToken());
        assertNotNull(response);
        assertEquals("admin", response.getSub());
    }

    @Test
    @Order(4)
    public void getRefreshTokens() throws AuthenticationException {
        tokenResponse = iamServiceManagerClient.getIAMAdminAuthorizerClient()
                .refreshTokens(tokenResponse.getRefreshToken(), ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertNotNull(tokenResponse.getTokenType().equals(TokenType.BEARER.getType()));
    }

    @Test
    @Order(5)
    public void verifyRefreshedTokens() throws IOException {
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
    @Order(6)
    public void revokeTokens() throws IOException {
        iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .revokeToken(tokenResponse.getRefreshToken());
        iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .revokeToken(tokenResponse.getAccessToken());
    }

    @Test
    @Order(7)
    public void verifyRevokedTokens() throws IOException {
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

}
