package itx.iamservice.examples.methodsecurity.ittests;


import itx.iamservice.core.dto.HealthCheckResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getHealthCheckResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MethodSecurityTestsIT {

    private static TestRestTemplate restTemplate;
    private static int iamServerPort;

    @BeforeAll
    public static void init() {
        restTemplate = new TestRestTemplate();
        iamServerPort = 8080;
    }

    @Test
    @Order(1)
    public void initialTestsIT() {
        HealthCheckResponse healthCheckResponse = getHealthCheckResponse(restTemplate, iamServerPort);
        assertNotNull(healthCheckResponse);
    }

}
