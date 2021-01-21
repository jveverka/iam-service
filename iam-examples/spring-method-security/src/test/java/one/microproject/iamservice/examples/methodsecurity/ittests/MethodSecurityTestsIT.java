package one.microproject.iamservice.examples.methodsecurity.ittests;


import one.microproject.iamservice.core.dto.CreateRole;
import one.microproject.iamservice.core.dto.CreateUser;
import one.microproject.iamservice.core.dto.PermissionInfo;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.UserProperties;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.examples.methodsecurity.dto.ServerData;
import one.microproject.iamservice.examples.methodsecurity.dto.SystemInfo;
import one.microproject.iamservice.serviceclient.IAMAuthorizerClient;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceProjectManagerClient;
import one.microproject.iamservice.serviceclient.IAMServiceUserManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static one.microproject.iamservice.examples.ittests.ITTestUtils.getGlobalAdminTokens;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static one.microproject.iamservice.examples.ittests.ITTestUtils.organizationId;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.projectId;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.clientId;
import static one.microproject.iamservice.examples.ittests.ITTestUtils.appAdminUserId;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MethodSecurityTestsIT {

    private static final Logger LOG = LoggerFactory.getLogger(MethodSecurityTestsIT.class);

    private static TestRestTemplate restTemplate;
    private static int iamServerPort;
    private static int resourceServerPort;
    private static TokenResponse appAdminTokens;
    private static TokenResponse appReaderTokens;
    private static TokenResponse appWriterTokens;

    private final static RoleId appUserRoleReader = RoleId.from("role-reader");
    private final static RoleId appUserRoleWriter = RoleId.from("role-writer");
    private final static Set<PermissionInfo> readerPermissions = Set.of(
            PermissionInfo.from("spring-method-security", "secure-data", "read")
    );
    private final static Set<PermissionInfo> writerPermissions = Set.of(
            PermissionInfo.from("spring-method-security", "secure-data", "read"),
            PermissionInfo.from("spring-method-security", "secure-data", "write")
    );
    private final static UserId appReaderUserId = UserId.from("bob-reader");
    private final static UserId appWriterUserId = UserId.from("alice-writer");

    private static IAMServiceManagerClient iamServiceManagerClient;

    @BeforeAll
    public static void init() throws MalformedURLException {
        restTemplate = new TestRestTemplate();
        iamServerPort = 8080;
        resourceServerPort = 8082;
        URL baseUrl = new URL("http://localhost:" + iamServerPort);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(2)
    void checkResourceServerIsAlive() {
        ResponseEntity<SystemInfo> response = restTemplate.getForEntity(
                "http://localhost:" + resourceServerPort + "/services/public/info", SystemInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    void getAppAdminUserTokens() throws IOException {
        IAMAuthorizerClient iamAuthorizerClient = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId);
        TokenResponseWrapper tokenResponseWrapper = iamAuthorizerClient.getAccessTokensOAuth2UsernamePassword(appAdminUserId.getId(), "secret", clientId, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        appAdminTokens = tokenResponseWrapper.getTokenResponse();
        LOG.info("App admin: ACCESS_TOKEN {}", appAdminTokens.getAccessToken());
        assertNotNull(appAdminTokens);
    }

    @Test
    @Order(6)
    void createOrdinaryAppUsers() {
        IAMServiceProjectManagerClient iamServiceProject = iamServiceManagerClient.getIAMServiceProject(appAdminTokens.getAccessToken(), organizationId, projectId);
        CreateRole createReaderRole = new CreateRole(appUserRoleReader.getId(), "Reader Role", readerPermissions);
        assertDoesNotThrow(() -> iamServiceProject.createRole(createReaderRole));
        CreateRole createWriterRole = new CreateRole(appUserRoleWriter.getId(), "Writer Role", writerPermissions);
        assertDoesNotThrow(() -> iamServiceProject.createRole(createWriterRole));
        IAMServiceUserManagerClient iamServiceUserManagerClient = iamServiceManagerClient.getIAMServiceUserManagerClient(appAdminTokens.getAccessToken(), organizationId, projectId);
        CreateUser createReaderUser = new CreateUser(appReaderUserId.getId(), "", 3600L, 3600L, "", "789456", UserProperties.getDefault());
        assertDoesNotThrow(() -> iamServiceUserManagerClient.createUser(createReaderUser));
        assertDoesNotThrow(() -> iamServiceUserManagerClient.addRoleToUser(appReaderUserId, appUserRoleReader));
        CreateUser createWriterUser = new CreateUser(appWriterUserId.getId(), "", 3600L, 3600L, "", "456789", UserProperties.getDefault());
        assertDoesNotThrow(() -> iamServiceUserManagerClient.createUser(createWriterUser));
        assertDoesNotThrow(() -> iamServiceUserManagerClient.addRoleToUser(appWriterUserId, appUserRoleWriter));
    }

    @Test
    @Order(7)
    void updateIamClientCache() {
        ResponseEntity<ServerData> response = restTemplate.getForEntity(
                "http://localhost:" + resourceServerPort + "/services/public/update-iam-client-cache", ServerData.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(8)
    void getAppReaderTokens() throws IOException {
        IAMAuthorizerClient iamAuthorizerClient = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId);
        TokenResponseWrapper tokenResponseWrapper = iamAuthorizerClient.getAccessTokensOAuth2UsernamePassword(appReaderUserId.getId(), "789456", clientId, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        appReaderTokens = tokenResponseWrapper.getTokenResponse();
        LOG.info("App READER: ACCESS_TOKEN {}", appReaderTokens.getAccessToken());
        assertNotNull(appReaderTokens);
    }

    @Test
    @Order(9)
    void getAppWriterTokens() throws IOException {
        IAMAuthorizerClient iamAuthorizerClient = iamServiceManagerClient.getIAMAuthorizerClient(organizationId, projectId);
        TokenResponseWrapper tokenResponseWrapper = iamAuthorizerClient.getAccessTokensOAuth2UsernamePassword(appWriterUserId.getId(), "456789", clientId, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        appWriterTokens = tokenResponseWrapper.getTokenResponse();
        LOG.info("App WRITER: ACCESS_TOKEN {}", appWriterTokens.getAccessToken());
        assertNotNull(appWriterTokens);
    }

    @Test
    @Order(10)
    void testUnSecureAccessNoAccessTokens() {
        ResponseEntity<ServerData> response = restTemplate.getForEntity(
                "http://localhost:" + resourceServerPort + "/services/secure/data", ServerData.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ServerData serverData = new ServerData("update");
        response = restTemplate.postForEntity(
                "http://localhost:" + resourceServerPort + "/services/secure/data", serverData, ServerData.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(11)
    void testSecureAccessInvalidIAMAdminTokens() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = getGlobalAdminTokens(iamServiceManagerClient);
        TokenResponse iamAdminTokens = tokenResponseWrapper.getTokenResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + iamAdminTokens.getAccessToken()));
        HttpEntity<ServerData> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<ServerData> response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.GET, requestEntity, ServerData.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ServerData serverData = new ServerData("update");
        requestEntity = new HttpEntity<>(serverData, headers);
        response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.POST, requestEntity, ServerData.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(12)
    void testSecureAccessInvalidIAppAdminTokens() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + appAdminTokens.getAccessToken()));
        HttpEntity<ServerData> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<ServerData> response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.GET, requestEntity, ServerData.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ServerData serverData = new ServerData("update");
        requestEntity = new HttpEntity<>(serverData, headers);
        response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.POST, requestEntity, ServerData.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Order(13)
    void testSecureAccessReaderUserTokensReadAccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + appReaderTokens.getAccessToken()));
        HttpEntity<ServerData> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<ServerData> response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.GET, requestEntity, ServerData.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
    }

    @Test
    @Order(14)
    void testSecureAccessReaderUserTokensWriteAccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + appReaderTokens.getAccessToken()));
        ServerData serverData = new ServerData("update");
        HttpEntity<ServerData> requestEntity = new HttpEntity<>(serverData, headers);
        ResponseEntity<ServerData> response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.POST, requestEntity, ServerData.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Order(15)
    void testSecureAccessWriterUserTokensReadAccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + appWriterTokens.getAccessToken()));
        HttpEntity<ServerData> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<ServerData> response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.GET, requestEntity, ServerData.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
    }

    @Test
    @Order(16)
    void testSecureAccessWriterUserTokensWriteAccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + appWriterTokens.getAccessToken()));
        ServerData serverData = new ServerData("update");
        HttpEntity<ServerData> requestEntity = new HttpEntity<>(serverData, headers);
        ResponseEntity<ServerData> response = restTemplate.exchange(
                "http://localhost:" + resourceServerPort + "/services/secure/data", HttpMethod.POST, requestEntity, ServerData.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("update", response.getBody().getData());
    }

}
