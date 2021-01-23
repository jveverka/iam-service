package itx.examples.webflux.tests;

import itx.examples.webflux.dto.CreateUserData;
import itx.examples.webflux.dto.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.MalformedURLException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecuredNegativeTestsIT {

    private static String baseUrl;

    @Autowired
    private WebTestClient webClient;

    @BeforeAll
    public static void init() throws MalformedURLException {
        baseUrl = "http://localhost:" + 8083;
    }

    @Test
    void getUsersEmpty() {
        EntityExchangeResult<UserData[]> entityExchangeResult = webClient.get().uri(baseUrl + "/secured/users")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(UserData[].class).returnResult();
    }

    @Test
    void createFirstUser() {
        CreateUserData createUserData = new CreateUserData("j@v.c", "juraj");
        EntityExchangeResult<UserData> entityExchangeResult = webClient.post().uri(baseUrl + "/secured/users")
                .bodyValue(createUserData)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(UserData.class).returnResult();
    }

    @Test
    void removeFirstUser() {
        EntityExchangeResult<UserData> entityExchangeResult = webClient.delete().uri(baseUrl + "/secured/users/{id}", "some-id")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(UserData.class).returnResult();
    }

}
