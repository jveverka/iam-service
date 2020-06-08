package itx.iamservice.server.tests;

import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.TokenResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    public static HttpHeaders createAuthorization(String jwt) {
        String authorizationHeader = "Bearer " + jwt;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", authorizationHeader);
        return requestHeaders;
    }

    public static OrganizationId createNewOrganization(String jwt, TestRestTemplate restTemplate, int port, String name) {
        CreateOrganizationRequest createOrganizationTest = new CreateOrganizationRequest(name);
        HttpEntity<CreateOrganizationRequest> requestEntity = new HttpEntity<>(createOrganizationTest, createAuthorization(jwt));

        ResponseEntity<OrganizationId> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/management/organizations",
                HttpMethod.POST,
                requestEntity,
                OrganizationId.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationId organizationId = response.getBody();
        assertNotNull(organizationId);
        assertNotNull(organizationId.getId());
        return organizationId;
    }

    public static void checkOrganizationCount(String jwt, TestRestTemplate restTemplate, int port, int expectedCount) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<OrganizationInfo[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/organizations",
                HttpMethod.GET,
                requestEntity,
                OrganizationInfo[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo[] organizationInfos = response.getBody();
        assertNotNull(organizationInfos);
        assertEquals(expectedCount, organizationInfos.length);
    }

    public static void checkOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<OrganizationInfo> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/organizations/" + organizationId.getId(),
                HttpMethod.GET,
                requestEntity,
                OrganizationInfo.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizationInfo organizationInfo = response.getBody();
        assertNotNull(organizationInfo);
        assertEquals(organizationId, organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getName());
        assertNotNull(organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getProjects());
        assertNotNull(organizationInfo.getX509Certificate());
    }

    public static void removeOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        restTemplate.exchange(
                "http://localhost:" + port + "/services/management/organizations/" + organizationId.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
    }

    public static void checkRemovedOrganization(String jwt, TestRestTemplate restTemplate, int port, OrganizationId organizationId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthorization(jwt));
        ResponseEntity<OrganizationInfo> response = restTemplate.exchange(
                "http://localhost:" + port + "/services/discovery/organizations/" + organizationId.getId(),
                HttpMethod.GET,
                requestEntity,
                OrganizationInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    public static TokenResponse getTokenResponseForUserNameAndPassword(TestRestTemplate restTemplate, int port) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("grant_type", "password");
        urlVariables.put("username", ModelUtils.IAM_ADMIN_USER.getId());
        urlVariables.put("password", "secret");
        urlVariables.put("scope", "");
        urlVariables.put("client_id", "admin-client");
        urlVariables.put("client_secret", "top-secret");
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/services/authentication/iam-admins/iam-admins/token" +
                        "?grant_type={grant_type}&username={username}&scope={scope}&password={password}&client_id={client_id}&client_secret={client_secret}",
                null, TokenResponse.class, urlVariables);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

}
