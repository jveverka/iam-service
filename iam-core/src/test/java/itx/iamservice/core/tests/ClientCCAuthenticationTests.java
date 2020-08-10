package itx.iamservice.core.tests;

import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.IdTokenRequest;
import itx.iamservice.core.dto.IntrospectRequest;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.services.dto.RevokeTokenRequest;
import itx.iamservice.core.services.dto.Scope;
import itx.iamservice.core.services.impl.caches.AuthorizationCodeCacheImpl;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.services.impl.caches.TokenCacheImpl;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.utils.TokenUtils;
import itx.iamservice.core.model.Tokens;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.impl.ClientServiceImpl;
import itx.iamservice.core.services.impl.ResourceServerServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.Security;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientCCAuthenticationTests {

    private static final String adminPassword = "top-secret";
    private static final String adminSecret = "top-secret";

    private static ModelCache modelCache;
    private static ClientService clientService;
    private static ResourceServerService resourceServerService;
    private static TokenCache tokenCache;
    private static AuthorizationCodeCache authorizationCodeCache;
    private static JWToken accessToken;
    private static JWToken refreshToken;
    private static IdTokenRequest idTokenRequest;

    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
        authorizationCodeCache = new AuthorizationCodeCacheImpl(10L, TimeUnit.MINUTES);
        modelCache = ModelUtils.createDefaultModelCache(adminPassword, adminSecret);
        tokenCache = new TokenCacheImpl(modelCache);
        clientService = new ClientServiceImpl(modelCache, tokenCache, authorizationCodeCache);
        resourceServerService = new ResourceServerServiceImpl(modelCache, tokenCache);
        idTokenRequest = new IdTokenRequest("http://localhost:8080/iam-service", "ad4u64s");
    }

    @Test
    @Order(1)
    @SuppressWarnings("unchecked")
    public void authenticateTest() {
        ClientCredentials clientCredentials = new ClientCredentials(ModelUtils.IAM_ADMIN_CLIENT_ID, adminSecret);
        Scope scope = new Scope(Set.of("iam-admin-client"));
        Optional<Tokens> tokensOptional = clientService.authenticate(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, clientCredentials, scope, idTokenRequest);
        assertTrue(tokensOptional.isPresent());
        DefaultClaims defaultClaims = TokenUtils.extractClaims(tokensOptional.get().getAccessToken());
        assertEquals(ModelUtils.IAM_ADMIN_CLIENT_ID.getId(), defaultClaims.getSubject());
        assertEquals(ModelUtils.IAM_ADMINS_ORG.getId(), defaultClaims.getIssuer());
        List<String> permissions = (List<String>)defaultClaims.get(TokenUtils.PERMISSIONS_CLAIM);
        assertNotNull(permissions);
        assertEquals(1, permissions.size());
        assertTrue(permissions.contains("iam-admin-service.organization.read"));
        assertFalse(permissions.contains("not-existing-role"));
        String type = (String)defaultClaims.get(TokenUtils.TYPE_CLAIM);
        assertEquals(TokenType.BEARER.getType(), type);
        accessToken = tokensOptional.get().getAccessToken();
        refreshToken = tokensOptional.get().getRefreshToken();
    }

    @Test
    @Order(2)
    public void verifyValidTokensTest() {
        IntrospectRequest requestAccessToken = new IntrospectRequest(accessToken, TokenType.BEARER);
        IntrospectRequest requestRefreshToken = new IntrospectRequest(refreshToken, TokenType.REFRESH);
        IntrospectResponse result = resourceServerService.introspect(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, requestAccessToken);
        assertTrue(result.getActive());
        result = resourceServerService.introspect(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, requestRefreshToken);
        assertTrue(result.getActive());
    }

    @Test
    @Order(3)
    public void logoutTest() {
        RevokeTokenRequest revokeAccessTokenRequest = new RevokeTokenRequest(accessToken, TokenType.BEARER);
        RevokeTokenRequest revokeRefreshTokenRequest = new RevokeTokenRequest(refreshToken, TokenType.REFRESH);
        boolean result = clientService.revoke(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, revokeAccessTokenRequest);
        assertTrue(result);
        result = clientService.revoke(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, revokeRefreshTokenRequest);
        assertTrue(result);
    }

    @Test
    @Order(4)
    public void verifyInvalidTokensTest() {
        IntrospectRequest requestAccessToken = new IntrospectRequest(accessToken, TokenType.BEARER);
        IntrospectRequest requestRefreshToken = new IntrospectRequest(refreshToken, TokenType.REFRESH);
        IntrospectResponse result = resourceServerService.introspect(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, requestAccessToken);
        assertFalse(result.getActive());
        result = resourceServerService.introspect(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, requestRefreshToken);
        assertFalse(result.getActive());
    }

}
