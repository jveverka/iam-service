package itx.iamservice.examples.resourceserver.ittests;

import itx.iamservice.examples.resourceserver.dto.SystemInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResourceServerTestsIT {

    private static TestRestTemplate restTemplate;
    private static int iamServerPort;
    private static int resourceServerPort;
    private static String jwt;

    @BeforeAll
    public static void init() {
        restTemplate = new TestRestTemplate();
        iamServerPort = 8080;
        resourceServerPort = 8081;
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
    public void checkResourceServerIsAliveTestsIT() {
        ResponseEntity<SystemInfo> response = restTemplate.getForEntity(
                "http://localhost:" + resourceServerPort + "/services/info", SystemInfo.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
