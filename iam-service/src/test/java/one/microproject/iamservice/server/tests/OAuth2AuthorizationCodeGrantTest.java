package one.microproject.iamservice.server.tests;

import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.services.dto.UserInfoResponse;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OAuth2AuthorizationCodeGrantTest {

    private static IAMServiceManagerClient iamServiceManagerClient;
    private static TokenResponse tokenResponse;
    private static AuthorizationCode authorizationCode;
    private static String redirectUri;
    private static String state = "1234";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void init() {
    }

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
    void getLoginFormTest() {
        redirectUri = "http://localhost:" + port + "/services/oauth2/iam-admins/iam-admins/redirect";
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("response_type", "code");
        urlVariables.put("scope", "");
        urlVariables.put("state", state);
        urlVariables.put("client_id", "admin-client");
        urlVariables.put("redirect_uri", redirectUri);
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/oauth2/iam-admins/iam-admins/authorize?response_type={response_type}&scope={scope}&state={state}&client_id={client_id}&redirect_uri={redirect_uri}",
                String.class, urlVariables);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(2)
    void getAuthorizationCodeTest() throws MalformedURLException, AuthenticationException {
        authorizationCode = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAuthorizationCodeOAuth2AuthorizationCodeGrant("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, Set.of(),
                        new URL("http://localhost:" + port + "/services/oauth2/" + ModelUtils.IAM_ADMINS_ORG.getId() + "/" + ModelUtils.IAM_ADMINS_PROJECT.getId() + "/redirect"), "123");
        assertNotNull(authorizationCode);
    }

    @Test
    @Order(3)
    void getProvideConsentTest() {
        assertDoesNotThrow(() -> iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .setOAuth2AuthorizationCodeGrantConsent(authorizationCode)
        );
    }

    @Test
    @Order(4)
    void getTokensTest() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2AuthorizationCodeGrant(authorizationCode.getCode(), state);
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
    @Order(5)
    void testUserInfo() throws IOException {
        UserInfoResponse response = iamServiceManagerClient
                .getIAMServiceStatusClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT)
                .getUserInfo(tokenResponse.getAccessToken());
        assertNotNull(response);
        assertEquals("admin", response.getSub());
    }

    @Test
    @Order(6)
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
    @Order(7)
    void getRefreshTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAdminAuthorizerClient()
                .refreshTokens(tokenResponse.getRefreshToken(), ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
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
    @Order(8)
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
    @Order(9)
    void revokeTokens() throws IOException {
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
    @Order(10)
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

}
