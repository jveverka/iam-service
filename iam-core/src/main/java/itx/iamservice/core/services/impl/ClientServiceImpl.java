package itx.iamservice.core.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.Tokens;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.Credentials;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.TokenUtils;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.dto.JWToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final Model model;
    private final TokenCache tokenCache;

    public ClientServiceImpl(Model model, TokenCache tokenCache) {
        this.model = model;
        this.tokenCache = tokenCache;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Tokens> authenticate(OrganizationId organizationId, ProjectId projectId, AuthenticationRequest authenticationRequest) {
        Optional<Project> projectOptional = model.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            Client client = authenticationRequest.getClient();
            boolean validationResult = projectOptional.get().verifyClientCredentials(client);
            if (!validationResult) {
                LOG.info("Invalid client {} credentials !", client.getId());
                return Optional.empty();
            }
        } else {
            LOG.info("Organization/Project {}/{} not found", organizationId, projectId);
            return Optional.empty();
        }
        Optional<User> userOptional = model.getUser(organizationId, projectId, authenticationRequest.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Credentials> credentials = user.getCredentials(authenticationRequest.getCredentialsType().getClass());
            if (credentials.isPresent()) {
                boolean valid = credentials.get().verify(authenticationRequest);
                if (valid) {
                    Set<RoleId> filteredRoles = TokenUtils.filterRoles(user.getRoles(), authenticationRequest.getScope());
                    Set<String> roles = filteredRoles.stream().map(roleId -> roleId.getId()).collect(Collectors.toSet());
                    JWToken accessToken = TokenUtils.issueToken(organizationId, projectId, user.getId(),
                            user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS,
                            roles, user.getPrivateKey(), TokenType.BEARER);
                    JWToken refreshToken = TokenUtils.issueToken(organizationId, projectId, user.getId(),
                            user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS,
                            roles, user.getPrivateKey(), TokenType.REFRESH);
                    Tokens token = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                            user.getDefaultAccessTokenDuration()/1000L, user.getDefaultRefreshTokenDuration()/1000L);
                    return Optional.of(token);
                }
            }
        } else {
            LOG.info("JWT subject {} not found", authenticationRequest.getUserId());
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<JWToken> refresh(OrganizationId organizationId, ProjectId projectId, JWToken token) {
        if (!tokenCache.isRevoked(token)) {
            DefaultClaims defaultClaims = TokenUtils.extractClaims(token);
            String subject = defaultClaims.getSubject();
            Optional<User> userOptional = model.getUser(organizationId, projectId, UserId.from(subject));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Jws<Claims>> claimsOptional = TokenUtils.verify(token, user.getCertificate().getPublicKey());
                LOG.info("JWT verified={}", claimsOptional.isPresent());
                if (claimsOptional.isPresent()) {
                    Claims claims = claimsOptional.get().getBody();
                    String tokenType = (String)claims.get(TokenUtils.TYPE_CLAIM);
                    if (TokenType.REFRESH.getType().equals(tokenType)) {
                        List<String> roles = (List<String>) claims.get(TokenUtils.ROLES_CLAIM);
                        JWToken renewedToken = TokenUtils.issueToken(organizationId, projectId, user.getId(),
                                user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS,
                                Set.copyOf(roles), user.getPrivateKey(), TokenType.BEARER);
                        return Optional.of(renewedToken);
                    } else {
                        LOG.info("Invalid JWT type {}, expected type {}", tokenType, TokenType.BEARER.getType());
                    }
                }
            } else {
                LOG.info("JWT subject {} not found", subject);
            }
        } else {
            LOG.info("JWT is revoked {}", token);
        }
        return Optional.empty();
    }

    @Override
    public boolean revoke(OrganizationId organizationId, ProjectId projectId, JWToken token) {
        DefaultClaims defaultClaims = TokenUtils.extractClaims(token);
        String subject = defaultClaims.getSubject();
        Optional<User> userOptional = model.getUser(organizationId, projectId, UserId.from(subject));
        if (userOptional.isPresent()) {
            Optional<Jws<Claims>> claims = TokenUtils.verify(token, userOptional.get().getCertificate().getPublicKey());
            LOG.info("JWT verified={}", claims.isPresent());
            if (claims.isPresent()) {
                tokenCache.addRevokedToken(token);
                return true;
            }
        } else {
            LOG.info("JWT subject {} not found", subject);
        }
        return false;
    }
}
