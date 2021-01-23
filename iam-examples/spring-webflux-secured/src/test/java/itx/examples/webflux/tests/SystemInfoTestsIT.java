package itx.examples.webflux.tests;

import itx.examples.webflux.dto.CreateUserData;
import itx.examples.webflux.dto.SystemInfo;
import itx.examples.webflux.dto.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SystemInfoTestsIT {

    private static String baseUrl;

    @BeforeAll
    public static void init() throws MalformedURLException {
        baseUrl = "http://localhost:" + 8083;
    }

    @Autowired
    private WebTestClient webClient;

    @Test
    @Order(1)
    @DisplayName("Get SystemInfo")
    void getUsersEmpty() {
        EntityExchangeResult<SystemInfo> entityExchangeResult = webClient.get().uri(baseUrl + "/services/public/info")
                .exchange()
                .expectStatus().isOk()
                .expectBody(SystemInfo.class).returnResult();
        assertNotNull(entityExchangeResult.getResponseBody());
    }

}
