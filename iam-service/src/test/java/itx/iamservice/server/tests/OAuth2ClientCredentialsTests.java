package itx.iamservice.server.tests;

import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.services.dto.TokenResponse;
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

import java.util.HashMap;
import java.util.Map;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenRevokeResponse;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokenVerificationResponse;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getTokensForClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuth2ClientCredentialsTests {

    private static TokenResponse tokenResponse;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void getTokens() {
        tokenResponse = getTokensForClient(restTemplate, port);
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
        assertNotNull(tokenResponse.getExpiresIn());
        assertNotNull(tokenResponse.getRefreshExpiresIn());
        assertNotNull(tokenResponse.getTokenType().equals(TokenType.BEARER.getType()));
    }

    @Test
    @Order(2)
    public void verifyTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
        response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
    }

    @Test
    @Order(3)
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
    @Order(4)
    public void verifyRefreshedTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
        response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getActive());
    }

    @Test
    @Order(5)
    public void revokeTokens() {
        ResponseEntity<Void> response = getTokenRevokeResponse(restTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        response = getTokenRevokeResponse(restTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void verifyRevokedTokens() {
        ResponseEntity<IntrospectResponse> response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getActive());
        response = getTokenVerificationResponse(restTemplate, port, tokenResponse.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getActive());
    }

}
