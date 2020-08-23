package itx.iamservice.server.tests;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.IAMClientBuilder;
import itx.iamservice.core.services.dto.TokenResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IAMServiceClientTests {

    private static TokenResponse tokenResponse;
    private static IAMClient iamClient;

    @LocalServerPort
    private static int port;

    @Autowired
    private static TestRestTemplate restTemplate;

    @BeforeAll
    public static void init() throws MalformedURLException {
        iamClient = IAMClientBuilder.builder()
                .setOrganizationId("org-01")
                .setProjectId("project-01")
                .withHttpProxy(new URL("http://localhost:" + port), 10L, TimeUnit.SECONDS)
                .build();
    }

}
