package itx.iamservice.core.tests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.ModelUtils;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.model.TokenCacheImpl;
import itx.iamservice.core.model.TokenUtils;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.dto.ClientInfo;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientAuthenticationTests {

    private static final String adminPassword = "top-secret";

    private static Model model;
    private static ClientService clientService;
    private static ResourceServerService resourceServerService;
    private static TokenCache tokenCache;
    private static JWToken token;

    @BeforeAll
    private static void init() throws PKIException {
        Security.addProvider(new BouncyCastleProvider());
        model = ModelUtils.createDefaultModel(adminPassword);
        tokenCache = new TokenCacheImpl(model);
        clientService = new ClientServiceImpl(model, tokenCache);
        resourceServerService = new ResourceServerServiceImpl(model, tokenCache);
    }

    @Test
    @Order(1)
    @SuppressWarnings("unchecked")
    public void authenticateTest() {
        Set<RoleId> scope = Set.of(RoleId.from("manage-organizations"), RoleId.from("manage-projects"), RoleId.from("not-existing-role"));
        UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest(ModelUtils.IAM_ADMIN_CLIENT, adminPassword, scope);
        Optional<JWToken> tokenOptional = clientService.authenticate(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, authenticationRequest);
        assertTrue(tokenOptional.isPresent());
        DefaultClaims defaultClaims = TokenUtils.extractClaims(tokenOptional.get());
        assertEquals(ModelUtils.IAM_ADMIN_CLIENT.getId(), defaultClaims.getSubject());
        assertEquals(ModelUtils.IAM_ADMINS_ORG.getId(), defaultClaims.getIssuer());
        assertEquals(ModelUtils.IAM_ADMINS_PROJECT.getId(), defaultClaims.getAudience());
        List<String> roles = (List<String>)defaultClaims.get(TokenUtils.ROLES_CLAIM);
        assertNotNull(roles);
        assertTrue(roles.size() == 2);
        assertTrue(roles.contains("manage-organizations"));
        assertTrue(roles.contains("manage-projects"));
        assertFalse(roles.contains("not-existing-role"));
        token = tokenOptional.get();
    }

    @Test
    @Order(2)
    public void verifyValidTokenTest() {
        boolean result = resourceServerService.verify(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, token);
        assertTrue(result);
    }

    @Test
    @Order(3)
    public void renewTokenTest() {
        Optional<JWToken> tokenOptional = clientService.renew(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, token);
        boolean result = resourceServerService.verify(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, token);
        assertFalse(result);
        assertTrue(tokenOptional.isPresent());
        assertFalse(token.equals(tokenOptional.get()));
        token = tokenOptional.get();
    }

    @Test
    @Order(4)
    public void verifyValidRenewedTokenTest() {
        boolean result = resourceServerService.verify(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, token);
        assertTrue(result);
    }

    @Test
    @Order(5)
    public void logoutTest() {
        boolean result = clientService.logout(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, token);
        assertTrue(result);
    }

    @Test
    @Order(6)
    public void verifyInvalidTokenTest() {
        boolean result = resourceServerService.verify(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, token);
        assertFalse(result);
    }

    @Test
    @Order(7)
    public void verifyInvalidTokenRenewTest() {
        Optional<JWToken> tokenOptional = clientService.renew(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, token);
        assertTrue(tokenOptional.isEmpty());
    }

    @Test
    @Order(8)
    public void externalTokenVerificationTest() {
        Optional<ProjectInfo> projectInfo = resourceServerService.getProjectInfo(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
        assertTrue(projectInfo.isPresent());
        Optional<ClientInfo> clientInfo = resourceServerService.getClientInfo(ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, ModelUtils.IAM_ADMIN_CLIENT);
        assertTrue(clientInfo.isPresent());
        Optional<Jws<Claims>> claims = TokenUtils.verify(token, clientInfo.get().getClientCertificate().getPublicKey());
        assertTrue(claims.isPresent());
        claims = TokenUtils.verify(token, projectInfo.get().getProjectCertificate().getPublicKey());
        assertTrue(claims.isEmpty());
        claims = TokenUtils.verify(token, projectInfo.get().getOrganizationCertificate().getPublicKey());
        assertTrue(claims.isEmpty());
    }

}
