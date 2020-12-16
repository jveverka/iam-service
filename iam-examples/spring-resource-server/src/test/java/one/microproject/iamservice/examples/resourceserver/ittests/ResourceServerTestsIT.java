package one.microproject.iamservice.examples.resourceserver.ittests;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.examples.resourceserver.dto.ServerData;
import one.microproject.iamservice.examples.resourceserver.dto.SystemInfo;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResourceServerTestsIT {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServerTestsIT.class);

    private static TestRestTemplate restTemplate;
    private static int iamServerPort;
    private static int resourceServerPort;
    private static TokenResponse iamAdminTokens;

    private static IAMServiceManagerClient iamServiceManagerClient;

    @BeforeAll
    public static void init() throws MalformedURLException {
        restTemplate = new TestRestTemplate();
        iamServerPort = 8080;
        resourceServerPort = 8081;
        URL baseUrl = new URL("http://localhost:" + iamServerPort);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(1)
    public void checkIamServerIsAliveTestsIT() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + iamServerPort + "/actuator/info", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void checkResourceServerIsAliveTestsITNoTokens() {
        ResponseEntity<SystemInfo> response = restTemplate.getForEntity(
                "http://localhost:" + resourceServerPort + "/services/public/info", SystemInfo.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(3)
    public void getIamAdminAccessTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        iamAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(iamAdminTokens);
        LOG.info("TOKEN: {}", iamAdminTokens.getAccessToken());
    }

    @Test
    @Order(4)
    public void checkResourceServerIsAliveTestsITWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + iamAdminTokens.getAccessToken()));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<SystemInfo> response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/public/info", HttpMethod.GET, requestEntity, SystemInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getName());
        assertNotNull(response.getBody().getVersion());
    }

    @Test
    @Order(5)
    public void checkResourceDataITNoTokens() {
        ResponseEntity<SystemInfo> response = restTemplate.getForEntity(
                "http://localhost:" + resourceServerPort + "/services/secure/data", SystemInfo.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void checkResourceDataITWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + iamAdminTokens.getAccessToken()));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<ServerData> response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.GET, requestEntity, ServerData.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
    }

}
