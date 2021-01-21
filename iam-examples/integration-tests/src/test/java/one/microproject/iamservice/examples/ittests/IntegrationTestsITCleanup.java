package one.microproject.iamservice.examples.ittests;

import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static one.microproject.iamservice.examples.ittests.ITTestUtils.getIAMServiceURL;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTestsITCleanup {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestsITCleanup.class);

    private static URL iamServerBaseURL;
    private static IAMServiceManagerClient iamServiceManagerClient;

    @BeforeAll
    public static void init() throws MalformedURLException {
        iamServerBaseURL = getIAMServiceURL();
        LOG.info("IAM BASE URL: {}", iamServerBaseURL);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(iamServerBaseURL)
                .withConnectionTimeout(10L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(0)
    void checkIamServerIsAliveBeforeCleanup() throws IOException {
        assertTrue(iamServiceManagerClient.isServerAlive());
    }

}
