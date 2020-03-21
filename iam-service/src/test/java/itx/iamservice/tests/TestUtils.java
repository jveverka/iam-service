package itx.iamservice.tests;

import itx.iamservice.services.dto.TokenRevokeResponse;
import itx.iamservice.services.dto.TokenVerificationResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public final class TestUtils {

    private TestUtils() {
    }

    public static ResponseEntity<TokenVerificationResponse> getTokenVerificationResponse(TestRestTemplate restTemplate, int port, String token) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("token", token);
        return restTemplate.postForEntity(
                "http://localhost:" + port + "/services/tokens/iam-admins/iam-admins/verify?token={token}",
                null, TokenVerificationResponse.class, urlVariables);
    }

    public static ResponseEntity<TokenRevokeResponse> getTokenRevokeResponse(TestRestTemplate restTemplate, int port, String token) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("token", token);
        return restTemplate.postForEntity(
                "http://localhost:" + port + "/services/tokens/iam-admins/iam-admins/revoke?token={token}",
                null, TokenRevokeResponse.class, urlVariables);
    }

}
