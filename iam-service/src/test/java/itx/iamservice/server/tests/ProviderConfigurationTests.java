package itx.iamservice.server.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.dto.ProviderConfigurationResponse;
import itx.iamservice.serviceclient.IAMServiceManagerClient;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import itx.iamservice.serviceclient.IAMServiceStatusClient;
import itx.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProviderConfigurationTests {

    private static OrganizationId organizationId;
    private static ProjectId projectId;
    private static IAMServiceManagerClient iamServiceManagerClient;
    private static IAMServiceStatusClient iamServiceStatusClient;

    @LocalServerPort
    private int port;

    @BeforeAll
    private static void init() {
        organizationId = ModelUtils.IAM_ADMINS_ORG;
        projectId = ModelUtils.IAM_ADMINS_PROJECT;
    }

    @Test
    @Order(1)
    public void initTest() throws MalformedURLException {
        URL baseUrl = new URL("http://localhost:" + port);
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        iamServiceStatusClient = iamServiceManagerClient.getIAMServiceStatusClient(organizationId, projectId);
    }

    @Test
    @Order(2)
    public void checkCreatedProjectTest() throws IOException {
        ProviderConfigurationResponse providerConfigurationResponse = iamServiceStatusClient.getProviderConfiguration();
        assertNotNull(providerConfigurationResponse);
        assertNotNull(providerConfigurationResponse.getIssuer());
        assertNotNull(providerConfigurationResponse.getAuthorizationEndpoint());
        assertNotNull(providerConfigurationResponse.getGrantTypesSupported());
        assertNotNull(providerConfigurationResponse.getResponseTypesSupported());
        assertNotNull(providerConfigurationResponse.getScopesSupported());
        assertNotNull(providerConfigurationResponse.getSubjectTypesSupported());
        assertNotNull(providerConfigurationResponse.getTokenEndpoint());
    }

    @Test
    @Order(3)
    public void checkGetJsonWebKeysTest() throws IOException {
        JWKResponse jwkResponse = iamServiceStatusClient.getJWK();
        assertNotNull(jwkResponse);
        assertNotNull(jwkResponse.getKeys());
        assertFalse(jwkResponse.getKeys().isEmpty());
    }

}
