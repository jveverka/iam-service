package itx.iamservice.server.tests;

import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.OrganizationInfo;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class TestUtils {

    private TestUtils() {
    }

    public static ResponseEntity<IntrospectResponse> getTokenVerificationResponse(TestRestTemplate restTemplate, int port, String token) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("token", token);
        return restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/introspect?token={token}",
                null, IntrospectResponse.class, urlVariables);
    }

    public static ResponseEntity<Void> getTokenRevokeResponse(TestRestTemplate restTemplate, int port, String token) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("token", token);
        return restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/revoke?token={token}",
                null, Void.class, urlVariables);
    }


    public static OrganizationId createNewOrganization(TestRestTemplate restTemplate, int port, String name) {
        CreateOrganizationRequest createOrganizationTest = new CreateOrganizationRequest(name);
        ResponseEntity<OrganizationId> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/management/organizations",
                createOrganizationTest, OrganizationId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationId organizationId = response.getBody();
        assertNotNull(organizationId);
        assertNotNull(organizationId.getId());
        return organizationId;
    }

    public static void checkOrganizationCount(TestRestTemplate restTemplate, int port, int expectedCount) {
        ResponseEntity<OrganizationInfo[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/organizations", OrganizationInfo[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo[] organizationInfos = response.getBody();
        assertNotNull(organizationInfos);
        assertEquals(expectedCount, organizationInfos.length);
    }

    public static void checkOrganization(TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        ResponseEntity<OrganizationInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/organizations/" + organizationId.getId(), OrganizationInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo organizationInfo = response.getBody();
        assertNotNull(organizationInfo);
        assertEquals(organizationId, organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getName());
        assertNotNull(organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getProjects());
        assertNotNull(organizationInfo.getX509Certificate());
    }

    public static void removeOrganization(TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        restTemplate.delete(
                "http://localhost:" + port + "/services/management/organizations/" + organizationId.getId());
    }

    public static void checkRemovedOrganization(TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        ResponseEntity<OrganizationInfo> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/services/discovery/organizations/" + organizationId.getId(), OrganizationInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
