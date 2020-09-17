package itx.iamservice.client.tests;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.IAMClientBuilder;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BuilderTests {

    @Test
    public void testBuilder() throws MalformedURLException, URISyntaxException {
        IAMClient iamClient = IAMClientBuilder.builder()
                .setBaseUrl(new URL("http://localhost:8080/iam"))
                .setOrganizationId("org-01")
                .setProjectId("project-01")
                .withHttpProxy(10L, TimeUnit.SECONDS)
                .build();
        assertNotNull(iamClient);
    }

}
