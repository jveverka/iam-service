package itx.iamservice.tests;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.JWKResponse;
import itx.iamservice.core.services.dto.ProviderConfigurationResponse;
import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProviderConfigurationTests {

    private static OrganizationId organizationId;
    private static ProjectId projectId;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    private static void init() {
        organizationId = ModelUtils.IAM_ADMINS_ORG;
        projectId = ModelUtils.IAM_ADMINS_PROJECT;
    }

    @Test
    @Order(1)
    public void checkCreatedProjectTest() {
        ResponseEntity<ProviderConfigurationResponse> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/.well-known/openid-configuration", ProviderConfigurationResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProviderConfigurationResponse providerConfigurationResponse = response.getBody();
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
    @Order(2)
    public void checkGetJsonWebKeysTest() {
        ResponseEntity<JWKResponse> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/.well-known/jwks.json", JWKResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JWKResponse jwkResponse = response.getBody();
        assertNotNull(jwkResponse);
        assertNotNull(jwkResponse.getKeys());
    }

}
