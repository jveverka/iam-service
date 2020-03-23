package itx.iamservice.core.tests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.AuthorizationCodeCacheImpl;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.services.caches.TokenCacheImpl;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.utils.TokenUtils;
import itx.iamservice.core.model.Tokens;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.core.services.dto.JWToken;
import itx.iamservice.core.services.dto.ProjectInfo;
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
public class ClientUPAuthenticationTests {

    private static final String adminPassword = "top-secret";
    private static final Set<RoleId> scope = Set.of(RoleId.from("manage-organizations"), RoleId.from("manage-projects"), RoleId.from("not-existing-role"));

    private static Model model;
    private static ClientService clientService;
    private static ResourceServerService resourceServerService;
    private static TokenCache tokenCache;
    private static AuthorizationCodeCache authorizationCodeCache;
    private static JWToken accessToken;
    private static JWToken refreshToken;

    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
        authorizationCodeCache = new AuthorizationCodeCacheImpl(10L, TimeUnit.MINUTES);
        model = ModelUtils.createDefaultModel(adminPassword);
        tokenCache = new TokenCacheImpl(model);
        clientService = new ClientServiceImpl(model, tokenCache, authorizationCodeCache);
        resourceServerService = new ResourceServerServiceImpl(model, tokenCache);
    }

    @Test
    @Order(1)
    @SuppressWarnings("unchecked")
    public void authenticateTest() {
        UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest(ModelUtils.IAM_ADMIN_USER, adminPassword, scope, ModelUtils.IAM_ADMIN_CLIENT_CREDENTIALS);
        Optional<Tokens> tokensOptional = clientService.authenticate(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, authenticationRequest);
        assertTrue(tokensOptional.isPresent());
        DefaultClaims defaultClaims = TokenUtils.extractClaims(tokensOptional.get().getAccessToken());
        assertEquals(ModelUtils.IAM_ADMIN_USER.getId(), defaultClaims.getSubject());
        assertEquals(ModelUtils.IAM_ADMINS_ORG.getId(), defaultClaims.getIssuer());
        assertEquals(ModelUtils.IAM_ADMINS_PROJECT.getId(), defaultClaims.getAudience());
        List<String> roles = (List<String>)defaultClaims.get(TokenUtils.ROLES_CLAIM);
        assertNotNull(roles);
        assertTrue(roles.size() == 2);
        assertTrue(roles.contains("manage-organizations"));
        assertTrue(roles.contains("manage-projects"));
        assertFalse(roles.contains("not-existing-role"));
        String type = (String)defaultClaims.get(TokenUtils.TYPE_CLAIM);
        assertEquals(TokenType.BEARER.getType(), type);
        accessToken = tokensOptional.get().getAccessToken();
        refreshToken = tokensOptional.get().getRefreshToken();
    }

    @Test
    @Order(2)
    public void verifyValidTokensTest() {
        boolean result = resourceServerService.verify(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, accessToken);
        assertTrue(result);
        result = resourceServerService.verify(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, refreshToken);
        assertTrue(result);
    }

    @Test
    @Order(3)
    public void refreshTokenTest() {
        Optional<Tokens> tokensOptional = clientService.refresh(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, ModelUtils.IAM_ADMIN_CLIENT_CREDENTIALS, refreshToken, scope);
        assertTrue(tokensOptional.isPresent());
        assertFalse(accessToken.equals(tokensOptional.get().getAccessToken()));
        accessToken = tokensOptional.get().getAccessToken();
    }

    @Test
    @Order(4)
    public void verifyValidRefreshedTokenTest() {
        boolean result = resourceServerService.verify(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, accessToken);
        assertTrue(result);
    }

    @Test
    @Order(5)
    public void logoutTest() {
        boolean result = clientService.revoke(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, accessToken);
        assertTrue(result);
    }

    @Test
    @Order(6)
    public void verifyInvalidTokenTest() {
        boolean result = resourceServerService.verify(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, accessToken);
        assertFalse(result);
    }

    @Test
    @Order(7)
    public void verifyInvalidTokenRenewTest() {
        Optional<Tokens> tokensOptional = clientService.refresh(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, ModelUtils.IAM_ADMIN_CLIENT_CREDENTIALS, accessToken, scope);
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
