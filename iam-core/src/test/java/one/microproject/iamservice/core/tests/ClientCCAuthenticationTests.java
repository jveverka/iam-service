package one.microproject.iamservice.core.tests;

import io.jsonwebtoken.impl.DefaultClaims;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.services.AuthenticationService;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.services.admin.ClientManagementService;
import one.microproject.iamservice.core.services.caches.AuthorizationCodeCache;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.dto.CreateClientRequest;
import one.microproject.iamservice.core.services.dto.IdTokenRequest;
import one.microproject.iamservice.core.dto.IntrospectRequest;
import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.services.dto.RevokeTokenRequest;
import one.microproject.iamservice.core.services.dto.Scope;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.services.impl.AuthenticationServiceImpl;
import one.microproject.iamservice.core.services.impl.TokenGeneratorImpl;
import one.microproject.iamservice.client.impl.TokenValidatorImpl;
import one.microproject.iamservice.core.services.impl.admin.ClientManagementServiceImpl;
import one.microproject.iamservice.core.services.impl.caches.AuthorizationCodeCacheImpl;
import one.microproject.iamservice.core.services.caches.TokenCache;
import one.microproject.iamservice.core.services.impl.caches.CacheHolderImpl;
import one.microproject.iamservice.core.services.impl.caches.TokenCacheImpl;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.model.utils.TokenUtils;
import one.microproject.iamservice.core.services.ResourceServerService;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.services.impl.ResourceServerServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Security;
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
    private static final String adminEmail = "admin@email.com";

    private static ModelCache modelCache;
    private static ResourceServerService resourceServerService;
    private static TokenCache tokenCache;
    private static AuthorizationCodeCache authorizationCodeCache;
    private static JWToken accessToken;
    private static JWToken refreshToken;
    private static IdTokenRequest idTokenRequest;
    private static AuthenticationService authenticationService;
    private static URI issuerUri;
    private static ClientManagementService clientManagementService;
    private static ClientId testClientId = ClientId.from("test-client-001");
    private static String testClientSecret = "6486231";
    private static TokenValidator tokenValidator;

    @BeforeAll
    private static void init() throws PKIException, URISyntaxException {
        Security.addProvider(new BouncyCastleProvider());
        tokenValidator = new TokenValidatorImpl();
        authorizationCodeCache = new AuthorizationCodeCacheImpl(10L, TimeUnit.MINUTES, new CacheHolderImpl<>());
        modelCache = ModelUtils.createDefaultModelCache(adminPassword, adminSecret, adminEmail);
        tokenCache = new TokenCacheImpl(modelCache, tokenValidator, new CacheHolderImpl<>());
        authenticationService = new AuthenticationServiceImpl(modelCache, tokenCache, authorizationCodeCache, new TokenGeneratorImpl(), tokenValidator);
        resourceServerService = new ResourceServerServiceImpl(modelCache, tokenCache, tokenValidator);
        idTokenRequest = new IdTokenRequest("http://localhost:8080/iam-service", "ad4u64s", "");
        issuerUri = new URI("http://localhost:8080/issuer");
        clientManagementService = new ClientManagementServiceImpl(modelCache);
        ClientProperties properties = ClientProperties.from("");
        CreateClientRequest request = new CreateClientRequest(testClientId, "", 3600L, 3600L, testClientSecret, properties);
        clientManagementService.createClient(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, request);
    }

    @Test
    @Order(1)
    @SuppressWarnings("unchecked")
    public void authenticateTest() {
        ClientCredentials clientCredentials = new ClientCredentials(testClientId, testClientSecret);
        String issuerClaim = issuerUri.toString();
        Scope scope = new Scope(Set.of("iam-admin-client"));
        Optional<TokenResponse> tokensOptional = authenticationService.authenticate(issuerUri, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, clientCredentials, scope, idTokenRequest);
        assertTrue(tokensOptional.isPresent());
        DefaultClaims defaultClaims = TokenUtils.extractClaims(JWToken.from(tokensOptional.get().getAccessToken()));
        assertEquals(testClientId.getId(), defaultClaims.getSubject());
        assertEquals(issuerClaim, defaultClaims.getIssuer());
        String scopeClaim = (String)defaultClaims.get(TokenUtils.SCOPE_CLAIM);
        assertNotNull(scopeClaim);
        String type = (String)defaultClaims.get(TokenUtils.TYPE_CLAIM);
        Assertions.assertEquals(TokenType.BEARER.getType(), type);
        accessToken = JWToken.from(tokensOptional.get().getAccessToken());
        refreshToken = JWToken.from(tokensOptional.get().getRefreshToken());
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
        boolean result = authenticationService.revoke(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, revokeAccessTokenRequest);
        assertTrue(result);
        result = authenticationService.revoke(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, revokeRefreshTokenRequest);
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
