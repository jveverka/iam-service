package itx.iamservice.core.tests;

import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.AuthenticationService;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.dto.IdTokenRequest;
import itx.iamservice.core.dto.IntrospectRequest;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.services.dto.RevokeTokenRequest;
import itx.iamservice.core.services.dto.Scope;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.core.services.impl.AuthenticationServiceImpl;
import itx.iamservice.core.services.impl.caches.AuthorizationCodeCacheImpl;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.services.impl.caches.TokenCacheImpl;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.utils.TokenUtils;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.impl.ResourceServerServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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

    private static ModelCache modelCache;
    private static ResourceServerService resourceServerService;
    private static TokenCache tokenCache;
    private static AuthorizationCodeCache authorizationCodeCache;
    private static JWToken accessToken;
    private static JWToken refreshToken;
    private static IdTokenRequest idTokenRequest;
    private static AuthenticationService authenticationService;
    private static URI issuerUri;

    @BeforeAll
    private static void init() throws PKIException, URISyntaxException {
        Security.addProvider(new BouncyCastleProvider());
        authorizationCodeCache = new AuthorizationCodeCacheImpl(10L, TimeUnit.MINUTES);
        modelCache = ModelUtils.createDefaultModelCache(adminPassword, adminSecret);
        tokenCache = new TokenCacheImpl(modelCache);
        authenticationService = new AuthenticationServiceImpl(modelCache, tokenCache, authorizationCodeCache);
        resourceServerService = new ResourceServerServiceImpl(modelCache, tokenCache);
        idTokenRequest = new IdTokenRequest("http://localhost:8080/iam-service", "ad4u64s");
        issuerUri = new URI("http://localhost:8080/issuer");
    }

    @Test
    @Order(1)
    @SuppressWarnings("unchecked")
    public void authenticateTest() {
        ClientCredentials clientCredentials = new ClientCredentials(ModelUtils.IAM_ADMIN_CLIENT_ID, adminSecret);
        String issuerClaim = issuerUri.toString();
        Scope scope = new Scope(Set.of("iam-admin-client"));
        Optional<TokenResponse> tokensOptional = authenticationService.authenticate(issuerUri, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, clientCredentials, scope, idTokenRequest);
        assertTrue(tokensOptional.isPresent());
        DefaultClaims defaultClaims = TokenUtils.extractClaims(JWToken.from(tokensOptional.get().getAccessToken()));
        assertEquals(ModelUtils.IAM_ADMIN_CLIENT_ID.getId(), defaultClaims.getSubject());
        assertEquals(issuerClaim, defaultClaims.getIssuer());
        String scopeClaim = (String)defaultClaims.get(TokenUtils.SCOPE_CLAIM);
        assertNotNull(scopeClaim);
        String type = (String)defaultClaims.get(TokenUtils.TYPE_CLAIM);
        assertEquals(TokenType.BEARER.getType(), type);
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
