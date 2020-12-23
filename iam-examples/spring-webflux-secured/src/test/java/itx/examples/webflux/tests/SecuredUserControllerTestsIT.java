package itx.examples.webflux.tests;

import itx.examples.webflux.dto.CreateUserData;
import itx.examples.webflux.dto.UserData;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecuredUserControllerTestsIT {

    private static final Logger LOG = LoggerFactory.getLogger(SecuredUserControllerTestsIT.class);

    private static String firstUserId;

    private static int iamServerPort;
    private static int resourceServerPort;
    private static TokenResponse iamAdminTokens;
    private static String baseUrl;

    private static IAMServiceManagerClient iamServiceManagerClient;

    @Autowired
    private WebTestClient webClient;

    @BeforeAll
    public static void init() throws MalformedURLException {
        iamServerPort = 8080;
        resourceServerPort = 8083;
        baseUrl = "http://localhost:" + resourceServerPort;
        URL baseUrl = new URL("http://localhost:" + iamServerPort);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(0)
    @DisplayName("Get Access Token")
    void getAccessToken() throws IOException {
        TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
        assertTrue(tokenResponseWrapper.isOk());
        iamAdminTokens = tokenResponseWrapper.getTokenResponse();
        assertNotNull(iamAdminTokens);
        LOG.info("TOKEN: {}", iamAdminTokens.getAccessToken());
    }

    @Test
    @Order(1)
    @DisplayName("Should get no users")
    void getUsersEmpty() {
        EntityExchangeResult<UserData[]> entityExchangeResult = webClient.get().uri(baseUrl + "/secured/users")
                .header("Authorization", "Bearer " + iamAdminTokens.getAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserData[].class).returnResult();
        assertEquals(0, entityExchangeResult.getResponseBody().length);
    }

    @Test
    @Order(2)
    @DisplayName("Create First User")
    void createFirstUser() {
        CreateUserData createUserData = new CreateUserData("j@v.c", "juraj");
        EntityExchangeResult<UserData> entityExchangeResult = webClient.post().uri(baseUrl + "/secured/users")
                .bodyValue(createUserData)
                .header("Authorization", "Bearer " + iamAdminTokens.getAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserData.class).returnResult();
        assertNotNull(entityExchangeResult.getResponseBody());
        assertNotNull(entityExchangeResult.getResponseBody().getId());
        assertEquals(createUserData.getEmail(), entityExchangeResult.getResponseBody().getEmail());
        assertEquals(createUserData.getName(), entityExchangeResult.getResponseBody().getName());
        firstUserId = entityExchangeResult.getResponseBody().getId();
    }

    @Test
    @Order(3)
    @DisplayName("Get users")
    void getUsersAll() {
        EntityExchangeResult<UserData[]> entityExchangeResult = webClient.get().uri(baseUrl + "/secured/users")
                .header("Authorization", "Bearer " + iamAdminTokens.getAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserData[].class).returnResult();
        assertEquals(1, entityExchangeResult.getResponseBody().length);
    }

    @Test
    @Order(4)
    @DisplayName("Remove first user")
    void removeFirstUser() {
        EntityExchangeResult<UserData> entityExchangeResult = webClient.delete().uri(baseUrl + "/secured/users/{id}", firstUserId)
                .header("Authorization", "Bearer " + iamAdminTokens.getAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserData.class).returnResult();
        assertNotNull(entityExchangeResult.getResponseBody());
        assertEquals(firstUserId, entityExchangeResult.getResponseBody().getId());
    }


}
