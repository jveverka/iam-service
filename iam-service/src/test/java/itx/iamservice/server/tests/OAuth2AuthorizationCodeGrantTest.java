package itx.iamservice.server.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.AuthorizationCodeGrantRequest;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.services.dto.ConsentRequest;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.core.services.dto.UserInfoResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenRevokeResponse;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenVerificationResponse;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getUserInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuth2AuthorizationCodeGrantTest {

    private static TokenResponse tokenResponse;
    private static AuthorizationCode authorizationCode;
    private static String redirectUri;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void init() {
    }

    @Test
    @Order(1)
    public void getLoginFormTest() {
        redirectUri = "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/token";
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("response_type", "code");
        urlVariables.put("scope", "");
        urlVariables.put("state", "1234");
        urlVariables.put("client_id", "admin-client");
        urlVariables.put("redirect_uri", redirectUri);
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/authorize?response_type={response_type}&scope={scope}&state={state}&client_id={client_id}&redirect_uri={redirect_uri}",
                String.class, urlVariables);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(2)
    public void getAuthorizationCodeTest() {
        AuthorizationCodeGrantRequest request = new AuthorizationCodeGrantRequest("admin", "secret", "admin-client", Set.of(), "123");
        HttpEntity<AuthorizationCodeGrantRequest> httpEntity = new HttpEntity(request);
        ResponseEntity<AuthorizationCode> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/authorize-programmatic",
                httpEntity, AuthorizationCode.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        authorizationCode = response.getBody();
        assertNotNull(authorizationCode);
    }

    @Test
    @Order(3)
    public void getProvideConsentTest() {
        ConsentRequest request = new ConsentRequest(authorizationCode.getCode(), authorizationCode.getAvailableScopes().getValues());
        HttpEntity<AuthorizationCodeGrantRequest> httpEntity = new HttpEntity(request);
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/consent-programmatic",
                httpEntity, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void getTokensTest() {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("grant_type", "authorization_code");
        urlVariables.put("code", authorizationCode.getCode().getCodeValue());
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/token?grant_type={grant_type}&code={code}",
                HttpMethod.POST,null, TokenResponse.class, urlVariables);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        tokenResponse = response.getBody();
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertNotNull(tokenResponse.getTokenType().equals(TokenType.BEARER.getType()));
    }

    @Test
    @Order(5)
    public void testUserInfo() {
        ResponseEntity<UserInfoResponse> response = getUserInfo(tokenResponse.getAccessToken(), restTemplate, port, OrganizationId.from("iam-admins"), ProjectId.from("iam-admins"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserInfoResponse userInfoResponse = response.getBody();
        assertNotNull(userInfoResponse);
        assertEquals("admin", userInfoResponse.getSub());
    }

    @Test
    @Order(6)
    public void verifyTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
        response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
    }

    @Test
    @Order(7)
    public void getRefreshTokens() {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("grant_type", "refresh_token");
        urlVariables.put("refresh_token", tokenResponse.getRefreshToken());
        urlVariables.put("scope", "");
        urlVariables.put("client_id", "admin-client");
        urlVariables.put("client_secret", "top-secret");
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/token" +
                        "?grant_type={grant_type}&refresh_token={refresh_token}&scope={scope}&client_id={client_id}&client_secret={client_secret}",
                null, TokenResponse.class, urlVariables);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        tokenResponse = response.getBody();
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertNotNull(tokenResponse.getTokenType().equals(TokenType.BEARER.getType()));
    }

    @Test
    @Order(8)
    public void verifyRefreshedTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
        response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
    }

    @Test
    @Order(9)
    public void revokeTokens() {
        ResponseEntity<Void> response = getTokenRevokeResponse(restTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        response = getTokenRevokeResponse(restTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(10)
    public void verifyRevokedTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getActive());
        response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getActive());
    }

}
