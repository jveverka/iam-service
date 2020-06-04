package itx.iamservice.server.tests;

import itx.iamservice.core.dto.IntrospectResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

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

}
