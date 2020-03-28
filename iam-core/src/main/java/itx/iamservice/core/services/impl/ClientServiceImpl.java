package itx.iamservice.core.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.Tokens;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.Credentials;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.utils.TokenUtils;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.AuthorizationCodeContext;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.JWToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final Model model;
    private final TokenCache tokenCache;
    private final AuthorizationCodeCache codeCache;

    public ClientServiceImpl(Model model, TokenCache tokenCache, AuthorizationCodeCache codeCache) {
        this.model = model;
        this.tokenCache = tokenCache;
        this.codeCache = codeCache;
    }

    @Override
    public Optional<Tokens> authenticate(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials, Set<RoleId> scope) {
        Optional<Project> projectOptional = model.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> clientOptional = projectOptional.get().getClient(clientCredentials.getId());
            if (clientOptional.isPresent()) {
                boolean validationResult = projectOptional.get().verifyClientCredentials(clientCredentials);
                if (validationResult) {
                    Client client = clientOptional.get();
                    Project project = projectOptional.get();
                    Set<RoleId> filteredRoles = TokenUtils.filterRoles(client.getRoles(), scope);
                    Set<String> roles = filteredRoles.stream().map(roleId -> roleId.getId()).collect(Collectors.toSet());
                    JWToken accessToken = TokenUtils.issueToken(organizationId, projectId, client.getId(),
                            client.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS,
                            roles, project.getPrivateKey(), TokenType.BEARER);
                    JWToken refreshToken = TokenUtils.issueToken(organizationId, projectId, client.getId(),
                            client.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS,
                            roles, project.getPrivateKey(), TokenType.REFRESH);
                    Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                            client.getDefaultAccessTokenDuration()/1000L, client.getDefaultRefreshTokenDuration()/1000L);
                    return Optional.of(tokens);
                } else {
                    LOG.info("Client {} credentials invalid !", clientCredentials.getId());
                }
            } else {
                LOG.info("ClientId {} not found within Organization/Project {}/{}", clientCredentials.getId(), organizationId, projectId);
            }
        } else {
            LOG.info("Organization/Project {}/{} not found", organizationId, projectId);
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Tokens> authenticate(OrganizationId organizationId, ProjectId projectId, AuthenticationRequest authenticationRequest) {
        Optional<Project> projectOptional = model.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            ClientCredentials clientCredentials = authenticationRequest.getClientCredentials();
            boolean validationResult = projectOptional.get().verifyClientCredentials(clientCredentials);
            if (!validationResult) {
                LOG.info("Invalid client {} credentials !", clientCredentials.getId());
                return Optional.empty();
            }
        } else {
            LOG.info("Organization/Project {}/{} not found", organizationId, projectId);
            return Optional.empty();
        }
        Optional<User> userOptional = model.getUser(organizationId, projectId, authenticationRequest.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Credentials> credentials = user.getCredentials(authenticationRequest.getCredentialsType());
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
                    Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                            user.getDefaultAccessTokenDuration()/1000L, user.getDefaultRefreshTokenDuration()/1000L);
                    return Optional.of(tokens);
                }
            }
        } else {
            LOG.info("JWT subject {} not found", authenticationRequest.getUserId());
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Tokens> refresh(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials, JWToken token, Set<RoleId> scope) {
        if (!tokenCache.isRevoked(token)) {
            Optional<Project> projectOptional = model.getProject(organizationId, projectId);
            if (projectOptional.isPresent()) {
                boolean validationResult = projectOptional.get().verifyClientCredentials(clientCredentials);
                if (!validationResult) {
                    LOG.info("Invalid client {} credentials !", clientCredentials.getId());
                    return Optional.empty();
                }
            } else {
                LOG.info("Organization/Project {}/{} not found", organizationId, projectId);
                return Optional.empty();
            }
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
                        Set<RoleId> filteredRoles = TokenUtils.filterRoles(user.getRoles(), scope);
                        Set<String> roles = filteredRoles.stream().map(roleId -> roleId.getId()).collect(Collectors.toSet());
                        JWToken accessToken = TokenUtils.issueToken(organizationId, projectId, user.getId(),
                                user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS,
                                roles, user.getPrivateKey(), TokenType.BEARER);
                        JWToken refreshToken = TokenUtils.issueToken(organizationId, projectId, user.getId(),
                                user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS,
                                roles, user.getPrivateKey(), TokenType.REFRESH);
                        Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                                user.getDefaultAccessTokenDuration()/1000L, user.getDefaultRefreshTokenDuration()/1000L);
                        return Optional.of(tokens);
                    } else {
                        LOG.info("Invalid JWT type {}, expected type {}", tokenType, TokenType.BEARER.getType());
                    }
                }
            } else {
                Optional<Client> clientOptional = projectOptional.get().getClient(clientCredentials.getId());
                if (clientOptional.isPresent()) {
                    boolean validationResult = projectOptional.get().verifyClientCredentials(clientCredentials);
                    if (validationResult) {
                        Client client = clientOptional.get();
                        Project project = projectOptional.get();
                        Set<RoleId> filteredRoles = TokenUtils.filterRoles(client.getRoles(), scope);
                        Set<String> roles = filteredRoles.stream().map(roleId -> roleId.getId()).collect(Collectors.toSet());
                        JWToken accessToken = TokenUtils.issueToken(organizationId, projectId, client.getId(),
                                client.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS,
                                roles, project.getPrivateKey(), TokenType.BEARER);
                        JWToken refreshToken = TokenUtils.issueToken(organizationId, projectId, client.getId(),
                                client.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS,
                                roles, project.getPrivateKey(), TokenType.REFRESH);
                        Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                                client.getDefaultAccessTokenDuration()/1000L, client.getDefaultRefreshTokenDuration()/1000L);
                        return Optional.of(tokens);
                    } else {
                        LOG.info("Client {} credentials invalid !", clientCredentials.getId());
                    }
                } else {
                    LOG.info("ClientId {} not found within Organization/Project {}/{}", clientCredentials.getId(), organizationId, projectId);
                }
                LOG.info("JWT subject {} not found", subject);
            }
        } else {
            LOG.info("JWT is revoked {}", token);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthorizationCode> login(OrganizationId organizationId, ProjectId projectId, UserId userId, ClientId clientId, String password, Set<RoleId> scope, String state) {
        Optional<Project> projectOptional = model.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> optionalClient = projectOptional.get().getClient(clientId);
            if (!optionalClient.isPresent()) {
                LOG.info("Invalid clientId {} !", clientId);
                return Optional.empty();
            }
        } else {
            LOG.info("Organization/Project {}/{} not found", organizationId, projectId);
            return Optional.empty();
        }
        Optional<User> userOptional = model.getUser(organizationId, projectId, userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Credentials> credentials = user.getCredentials(UPCredentials.class);
            if (credentials.isPresent()) {
                UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest(userId, password, scope, null);
                boolean valid = credentials.get().verify(authenticationRequest);
                if (valid) {
                    Set<RoleId> filteredRoles = TokenUtils.filterRoles(user.getRoles(), scope);
                    AuthorizationCode authorizationCode = codeCache.issue(organizationId, projectId, userId, state, filteredRoles);
                    return Optional.of(authorizationCode);
                }
            }
        } else {
            LOG.info("JWT subject {} not found", userId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Tokens> authenticate(Code code) {
        Optional<AuthorizationCodeContext> contextOptional = codeCache.verifyAndRemove(code);
        if (contextOptional.isPresent()) {
            AuthorizationCodeContext context = contextOptional.get();
            Optional<User> optionalUser = model.getUser(context.getOrganizationId(), context.getProjectId(), context.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Set<String> roles = context.getRoles().stream().map(r -> r.getId()).collect(Collectors.toSet());
                JWToken accessToken = TokenUtils.issueToken(context.getOrganizationId(), context.getProjectId(), user.getId(),
                        user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS,
                        roles, user.getPrivateKey(), TokenType.BEARER);
                JWToken refreshToken = TokenUtils.issueToken(context.getOrganizationId(), context.getProjectId(), user.getId(),
                        user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS,
                        roles, user.getPrivateKey(), TokenType.REFRESH);
                Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                        user.getDefaultAccessTokenDuration() / 1000L, user.getDefaultRefreshTokenDuration() / 1000L);
                return Optional.of(tokens);
            } else {
                LOG.info("User {} not found", context.getUserId());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean revoke(OrganizationId organizationId, ProjectId projectId, JWToken token) {
        DefaultClaims defaultClaims = TokenUtils.extractClaims(token);
        String subject = defaultClaims.getSubject();
        Optional<User> userOptional = model.getUser(organizationId, projectId, UserId.from(subject));
        if (userOptional.isPresent()) {
            Optional<Jws<Claims>> claimsJws = TokenUtils.verify(token, userOptional.get().getCertificate().getPublicKey());
            LOG.info("JWT verified={}", claimsJws.isPresent());
            if (claimsJws.isPresent()) {
                tokenCache.addRevokedToken(token);
                return true;
            }
        } else {
            ClientId clientId = ClientId.from(defaultClaims.getSubject());
            Optional<Client> clientOptional = this.model.getClient(organizationId, projectId, clientId);
            Optional<Project> projectOptional = this.model.getProject(organizationId, projectId);
            if (projectOptional.isPresent() && clientOptional.isPresent()) {
                Optional<Jws<Claims>> claimsJws = TokenUtils.verify(token, projectOptional.get().getCertificate().getPublicKey());
                LOG.info("JWT verified={}", claimsJws.isPresent());
                if (claimsJws.isPresent()) {
                    tokenCache.addRevokedToken(token);
                    return true;
                }
            }
            LOG.info("JWT subject {} not found", subject);
        }
        return false;
    }
}
