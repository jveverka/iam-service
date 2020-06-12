package itx.iamservice.server.tests;

import itx.iamservice.core.dto.HealthCheckResponse;
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

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getHealthCheckResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void getHealthCheck() {
        HealthCheckResponse healthCheckResponse = getHealthCheckResponse(restTemplate, port);
        assertNotNull(healthCheckResponse);
        assertNotNull(healthCheckResponse.getId());
        assertNotNull(healthCheckResponse.getName());
        assertNotNull(healthCheckResponse.getTimestamp());
        assertEquals("OK", healthCheckResponse.getStatus());
        assertEquals("iam-service", healthCheckResponse.getType());
        assertEquals("1.0.0",  healthCheckResponse.getVersion());
    }

}
