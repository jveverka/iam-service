package itx.iamservice.core.tests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.User;
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
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
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
public class ClientUPAuthenticationTests {

    private static final String adminPassword = "top-secret";
    private static final String adminSecret = "top-secret";

    private static final Scope scope = new Scope(Set.of("manage-organizations", "manage-projects", "not-existing-role"));

    private static ModelCache modelCache;
    private static AuthenticationService authenticationService;
    private static ResourceServerService resourceServerService;
    private static TokenCache tokenCache;
    private static AuthorizationCodeCache authorizationCodeCache;
    private static JWToken accessToken;
    private static JWToken refreshToken;
    private static IdTokenRequest idTokenRequest;
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
        String issuerClaim = issuerUri.toString();
        ClientCredentials clientCredentials = new ClientCredentials(ModelUtils.IAM_ADMIN_CLIENT_ID, adminSecret);
        UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest(ModelUtils.IAM_ADMIN_USER, adminPassword, scope, clientCredentials);
        Optional<TokenResponse> tokensOptional = authenticationService.authenticate(issuerUri, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, clientCredentials, new Scope(Set.of()), authenticationRequest, idTokenRequest);
        assertTrue(tokensOptional.isPresent());
        DefaultClaims defaultClaims = TokenUtils.extractClaims(JWToken.from(tokensOptional.get().getAccessToken()));
        assertEquals(ModelUtils.IAM_ADMIN_USER.getId(), defaultClaims.getSubject());
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
    public void refreshTokenTest() {
        ClientCredentials clientCredentials = new ClientCredentials(ModelUtils.IAM_ADMIN_CLIENT_ID, adminSecret);
        Optional<TokenResponse> tokensOptional = authenticationService.refreshTokens(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, refreshToken, clientCredentials, scope, idTokenRequest);
        assertTrue(tokensOptional.isPresent());
        assertFalse(accessToken.equals(tokensOptional.get().getAccessToken()));
        accessToken = JWToken.from(tokensOptional.get().getAccessToken());
    }

    @Test
    @Order(4)
    public void verifyValidRefreshedTokenTest() {
        IntrospectRequest requestAccessToken = new IntrospectRequest(accessToken, TokenType.BEARER);
        IntrospectResponse result = resourceServerService.introspect(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, requestAccessToken);
        assertTrue(result.getActive());
    }

    @Test
    @Order(5)
    public void logoutTest() {
        RevokeTokenRequest revokeAccessTokenRequest = new RevokeTokenRequest(accessToken, TokenType.BEARER);
        boolean result = authenticationService.revoke(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, revokeAccessTokenRequest);
        assertTrue(result);
    }

    @Test
    @Order(6)
    public void verifyInvalidTokenTest() {
        IntrospectRequest requestAccessToken = new IntrospectRequest(accessToken, TokenType.BEARER);
        IntrospectResponse result = resourceServerService.introspect(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, requestAccessToken);
        assertFalse(result.getActive());
    }

    @Test
    @Order(7)
    public void verifyInvalidTokenRenewTest() {
        ClientCredentials clientCredentials = new ClientCredentials(ModelUtils.IAM_ADMIN_CLIENT_ID, adminSecret);
        Optional<TokenResponse> tokensOptional = authenticationService.refreshTokens(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, accessToken, clientCredentials, scope, idTokenRequest);
        assertTrue(tokensOptional.isEmpty());
    }

    @Test
    @Order(8)
    public void externalTokenVerificationTest() {
        Optional<Organization> organizationOptional = resourceServerService.getOrganization(ModelUtils.IAM_ADMINS_ORG);
        assertTrue(organizationOptional.isPresent());
        Optional<Project> projectInfo = resourceServerService.getProject(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
        assertTrue(projectInfo.isPresent());
        Optional<User> userInfo = resourceServerService.getUser(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, ModelUtils.IAM_ADMIN_USER);
        assertTrue(userInfo.isPresent());
        Optional<Jws<Claims>> claims = TokenUtils.verify(accessToken, userInfo.get().getCertificate().getPublicKey());
        assertTrue(claims.isPresent());
        claims = TokenUtils.verify(accessToken, projectInfo.get().getCertificate().getPublicKey());
        assertTrue(claims.isEmpty());
        claims = TokenUtils.verify(accessToken, organizationOptional.get().getCertificate().getPublicKey());
        assertTrue(claims.isEmpty());
    }

}
