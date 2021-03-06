package one.microproject.iamservice.client.tests;

import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.client.IAMClientBuilder;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BuilderTests {

    @Test
    void testBuilder() throws MalformedURLException, URISyntaxException {
        IAMClient iamClient = IAMClientBuilder.builder()
                .setOrganizationId("org-01")
                .setProjectId("project-01")
                .withHttpProxy(new URL("http://localhost:8080/iam"), 10L, TimeUnit.SECONDS)
                .build();
        assertNotNull(iamClient);
    }

}
