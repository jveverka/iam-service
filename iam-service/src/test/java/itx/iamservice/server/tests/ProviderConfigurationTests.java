package itx.iamservice.server.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.dto.ProviderConfigurationResponse;
import itx.iamservice.serviceclient.IAMServiceClient;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import itx.iamservice.serviceclient.impl.AuthenticationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProviderConfigurationTests {

    private static OrganizationId organizationId;
    private static ProjectId projectId;
    private static IAMServiceClient iamServiceClient;

    @LocalServerPort
    private int port;

    @BeforeAll
    private static void init() {
        organizationId = ModelUtils.IAM_ADMINS_ORG;
        projectId = ModelUtils.IAM_ADMINS_PROJECT;
    }

    @Test
    @Order(1)
    public void initTest() throws AuthenticationException {
        String baseUrl = "http://localhost:" + port;
        iamServiceClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    @Test
    @Order(2)
    public void checkCreatedProjectTest() throws IOException {
        ProviderConfigurationResponse providerConfigurationResponse = iamServiceClient.getProviderConfiguration(organizationId, projectId);
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
        JWKResponse jwkResponse = iamServiceClient.getJWK(organizationId, projectId);
        assertNotNull(jwkResponse);
        assertNotNull(jwkResponse.getKeys());
        assertFalse(jwkResponse.getKeys().isEmpty());
    }

}
