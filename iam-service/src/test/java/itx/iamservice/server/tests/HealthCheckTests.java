package itx.iamservice.server.tests;

import itx.iamservice.serviceclient.IAMServiceClient;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckTests {

    @LocalServerPort
    private int port;

    private static IAMServiceClient iamServiceClient;

    @Test
    @Order(1)
    public void initTest() {
        String baseUrl = "http://localhost:" + port;
        iamServiceClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(2)
    public void getHealthCheck() throws IOException {
        String response = iamServiceClient.getActuatorInfo();
        assertNotNull(response);
    }

}
