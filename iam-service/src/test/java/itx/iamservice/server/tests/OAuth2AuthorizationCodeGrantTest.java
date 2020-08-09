package itx.iamservice.server.tests;

import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.dto.IntrospectResponse;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenRevokeResponse;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenVerificationResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuth2AuthorizationCodeGrantTest {

    private static TokenResponse tokenResponse;
    private static Code code;
    private static String redirectUri;
    private static RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate( new SimpleClientHttpRequestFactory(){
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod ) {
                connection.setInstanceFollowRedirects(false);
            }
        });
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
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("username", "admin");
        urlVariables.put("password", "secret");
        urlVariables.put("client_id", "admin-client");
        urlVariables.put("redirect_uri", redirectUri);
        urlVariables.put("state", "1234");
        ResponseEntity<Object> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/login?state={state}&username={username}&password={password}&client_id={client_id}&redirect_uri={redirect_uri}",
                HttpMethod.GET, null, Object.class, urlVariables);
        assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
        URI redirectedUri = response.getHeaders().getLocation();
        assertNotNull(redirectedUri);
        assertTrue(redirectedUri.toString().startsWith(redirectUri));
        MultiValueMap<String, String> queryParameters = UriComponentsBuilder.fromUri(redirectedUri).build().getQueryParams();
        String codeString = queryParameters.getFirst("code");
        String stateString = queryParameters.getFirst("state");
        assertNotNull(codeString);
        assertNotNull(stateString);
        code = new Code(codeString);
    }

    @Test
    @Order(3)
    public void getTokensTest() {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("grant_type", "authorization_code");
        urlVariables.put("code", code.getCodeValue());
        ResponseEntity<TokenResponse> response = testRestTemplate.exchange(
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
    @Order(4)
    public void verifyTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(testRestTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
        response = getTokenVerificationResponse(testRestTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
    }

    @Test
    @Order(5)
    public void getRefreshTokens() {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("grant_type", "refresh_token");
        urlVariables.put("refresh_token", tokenResponse.getRefreshToken());
        urlVariables.put("scope", "");
        urlVariables.put("client_id", "admin-client");
        urlVariables.put("client_secret", "top-secret");
        ResponseEntity<TokenResponse> response = testRestTemplate.postForEntity(
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
    @Order(6)
    public void verifyRefreshedTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(testRestTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
        response = getTokenVerificationResponse(testRestTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
    }

    @Test
    @Order(7)
    public void revokeTokens() {
        ResponseEntity<Void> response = getTokenRevokeResponse(testRestTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        response = getTokenRevokeResponse(testRestTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(8)
    public void verifyRevokedTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(testRestTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getActive());
        response = getTokenVerificationResponse(testRestTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getActive());
    }

}
