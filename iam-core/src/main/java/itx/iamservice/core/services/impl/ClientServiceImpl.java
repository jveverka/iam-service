package itx.iamservice.core.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.AuthenticationRequest;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.Tokens;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.Credentials;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.utils.TokenUtils;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.AuthorizationCodeContext;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.IdTokenRequest;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.dto.RevokeTokenRequest;
import itx.iamservice.core.services.dto.Scope;
import itx.iamservice.core.services.dto.UserInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static itx.iamservice.core.model.utils.TokenUtils.TYPE_CLAIM;

public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ModelCache modelCache;
    private final TokenCache tokenCache;
    private final AuthorizationCodeCache codeCache;

    public ClientServiceImpl(ModelCache modelCache, TokenCache tokenCache, AuthorizationCodeCache codeCache) {
        this.modelCache = modelCache;
        this.tokenCache = tokenCache;
        this.codeCache = codeCache;
    }

    @Override
    public Optional<Tokens> authenticate(OrganizationId organizationId, ProjectId projectId,
                                         ClientCredentials clientCredentials, Scope scope, IdTokenRequest idTokenRequest) {
        Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> clientOptional = modelCache.getClient(organizationId, projectId, clientCredentials.getId());
            if (clientOptional.isPresent()) {
                boolean validationResult = modelCache.verifyClientCredentials(organizationId, projectId, clientCredentials);
                if (validationResult) {
                    Client client = clientOptional.get();
                    Project project = projectOptional.get();
                    Set<Permission> clientPermissions = modelCache.getPermissions(organizationId, projectId, client.getId());
                    Scope filteredScopes = TokenUtils.filterScopes(clientPermissions, scope);
                    KeyPairData keyPairData = project.getKeyPairData();
                    JWToken accessToken = TokenUtils.issueToken(organizationId, project.getId(), project.getAudience(), client.getId(),
                            client.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                            null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.BEARER);
                    JWToken refreshToken = TokenUtils.issueToken(organizationId, project.getId(), project.getAudience(), client.getId(),
                            client.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                            null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.REFRESH);
                    JWToken idToken = TokenUtils.issueIdToken(organizationId, projectId, client.getId(), client.getId().getId(),
                            client.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, idTokenRequest,
                            keyPairData.getId(), keyPairData.getPrivateKey());
                    Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                            client.getDefaultAccessTokenDuration()/1000L, client.getDefaultRefreshTokenDuration()/1000L, idToken);
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
    public Optional<Tokens> authenticate(OrganizationId organizationId, ProjectId projectId,
                                         AuthenticationRequest authenticationRequest, IdTokenRequest idTokenRequest) {
        Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            ClientCredentials clientCredentials = authenticationRequest.getClientCredentials();
            boolean validationResult = modelCache.verifyClientCredentials(organizationId, projectId, clientCredentials);
            if (!validationResult) {
                LOG.info("Invalid client {} credentials !", clientCredentials.getId());
                return Optional.empty();
            }
        } else {
            LOG.info("Organization/Project {}/{} not found", organizationId, projectId);
            return Optional.empty();
        }
        Optional<User> userOptional = modelCache.getUser(organizationId, projectId, authenticationRequest.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Credentials> credentials = user.getCredentials(authenticationRequest.getCredentialsType());
            if (credentials.isPresent()) {
                boolean valid = credentials.get().verify(authenticationRequest);
                if (valid) {
                    Set<Permission> userPermissions = modelCache.getPermissions(organizationId, projectId, user.getId());
                    Scope filteredScopes = TokenUtils.filterScopes(userPermissions, authenticationRequest.getScope());
                    KeyPairData keyPairData = user.getKeyPairData();
                    JWToken accessToken = TokenUtils.issueToken(organizationId, projectOptional.get().getId(), projectOptional.get().getAudience(), user.getId(),
                            user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                            null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.BEARER);
                    JWToken refreshToken = TokenUtils.issueToken(organizationId, projectOptional.get().getId(), projectOptional.get().getAudience(), user.getId(),
                            user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                            null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.REFRESH);
                    JWToken idToken = TokenUtils.issueIdToken(organizationId, projectId, authenticationRequest.getClientCredentials().getId(),
                            user.getId().getId(), user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, idTokenRequest,
                            keyPairData.getId(), keyPairData.getPrivateKey());
                    Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                            user.getDefaultAccessTokenDuration()/1000L, user.getDefaultRefreshTokenDuration()/1000L, idToken);
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
    public Optional<Tokens> refresh(OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials,
                                    JWToken token, Scope scope, IdTokenRequest idTokenRequest) {
        if (!tokenCache.isRevoked(token)) {
            Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
            if (projectOptional.isPresent()) {
                boolean validationResult = modelCache.verifyClientCredentials(organizationId, projectId, clientCredentials);
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
            Optional<User> userOptional = modelCache.getUser(organizationId, projectId, UserId.from(subject));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Jws<Claims>> claimsOptional = TokenUtils.verify(token, user.getCertificate().getPublicKey());
                LOG.info("JWT verified={}", claimsOptional.isPresent());
                if (claimsOptional.isPresent()) {
                    Claims claims = claimsOptional.get().getBody();
                    String tokenType = (String)claims.get(TYPE_CLAIM);
                    if (TokenType.REFRESH.getType().equals(tokenType)) {
                        Set<Permission> userPermissions = modelCache.getPermissions(organizationId, projectId, user.getId());
                        Scope filteredScopes = TokenUtils.filterScopes(userPermissions, scope);
                        KeyPairData keyPairData = user.getKeyPairData();
                        JWToken accessToken = TokenUtils.issueToken(organizationId, projectOptional.get().getId(), projectOptional.get().getAudience(), user.getId(),
                                user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.BEARER);
                        JWToken refreshToken = TokenUtils.issueToken(organizationId, projectOptional.get().getId(), projectOptional.get().getAudience(), user.getId(),
                                user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.REFRESH);
                        JWToken idToken = TokenUtils.issueIdToken(organizationId, projectId, clientCredentials.getId(), user.getId().getId(),
                                user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, idTokenRequest,
                                keyPairData.getId(), keyPairData.getPrivateKey());
                        Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                                user.getDefaultAccessTokenDuration()/1000L, user.getDefaultRefreshTokenDuration()/1000L, idToken);
                        return Optional.of(tokens);
                    } else {
                        LOG.info("Invalid JWT type {}, expected type {}", tokenType, TokenType.BEARER.getType());
                    }
                }
            } else {
                Optional<Client> clientOptional = modelCache.getClient(organizationId, projectId, clientCredentials.getId());
                if (clientOptional.isPresent()) {
                    boolean validationResult = modelCache.verifyClientCredentials(organizationId, projectId, clientCredentials);
                    if (validationResult) {
                        Client client = clientOptional.get();
                        Project project = projectOptional.get();
                        Set<Permission> clientPermissions = modelCache.getPermissions(organizationId, projectId, client.getId());
                        Scope filteredScopes = TokenUtils.filterScopes(clientPermissions, scope);
                        KeyPairData keyPairData = project.getKeyPairData();
                        JWToken accessToken = TokenUtils.issueToken(organizationId, project.getId(), project.getAudience(), client.getId(),
                                client.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.BEARER);
                        JWToken refreshToken = TokenUtils.issueToken(organizationId, project.getId(), project.getAudience(), client.getId(),
                                client.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, filteredScopes,
                                null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.REFRESH);
                        JWToken idToken = TokenUtils.issueIdToken(organizationId, projectId, client.getId(), client.getId().getId(),
                                client.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, idTokenRequest,
                                keyPairData.getId(), keyPairData.getPrivateKey());
                        Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                                client.getDefaultAccessTokenDuration()/1000L, client.getDefaultRefreshTokenDuration()/1000L, idToken);
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
    public Optional<AuthorizationCode> login(OrganizationId organizationId, ProjectId projectId, UserId userId,
                                             ClientId clientId, String password, Scope scope, String state, String redirectURI) {
        Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> optionalClient = modelCache.getClient(organizationId, projectId, clientId);
            if (!optionalClient.isPresent()) {
                LOG.info("Invalid clientId {} !", clientId);
                return Optional.empty();
            }
        } else {
            LOG.info("Organization/Project {}/{} not found", organizationId, projectId);
            return Optional.empty();
        }
        Optional<User> userOptional = modelCache.getUser(organizationId, projectId, userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Credentials> credentials = user.getCredentials(UPCredentials.class);
            if (credentials.isPresent()) {
                UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest(userId, password, scope, null);
                boolean valid = credentials.get().verify(authenticationRequest);
                if (valid) {
                    Set<Permission> userPermissions = modelCache.getPermissions(organizationId, projectId, user.getId());
                    Scope filteredScopes = TokenUtils.filterScopes(userPermissions, scope);
                    AuthorizationCode authorizationCode = codeCache.issue(organizationId, projectId, clientId, userId, state, filteredScopes, projectOptional.get().getAudience(), redirectURI);
                    return Optional.of(authorizationCode);
                }
            }
        } else {
            LOG.info("JWT subject {} not found", userId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Tokens> authenticate(Code code, IdTokenRequest idTokenRequest) {
        Optional<AuthorizationCodeContext> contextOptional = codeCache.get(code);
        if (contextOptional.isPresent()) {
            AuthorizationCodeContext context = contextOptional.get();
            Optional<User> optionalUser = modelCache.getUser(context.getOrganizationId(), context.getProjectId(), context.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                KeyPairData keyPairData = user.getKeyPairData();
                JWToken accessToken = TokenUtils.issueToken(context.getOrganizationId(), context.getProjectId(), context.getAudience(), user.getId(),
                        user.getDefaultAccessTokenDuration(), TimeUnit.MILLISECONDS, context.getScope(),
                        null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.BEARER);
                JWToken refreshToken = TokenUtils.issueToken(context.getOrganizationId(), context.getProjectId(), context.getAudience(), user.getId(),
                        user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, context.getScope(),
                        null, keyPairData.getId(), keyPairData.getPrivateKey(), TokenType.REFRESH);
                JWToken idToken = TokenUtils.issueIdToken(context.getOrganizationId(), context.getProjectId(), context.getClientId(), user.getId().getId(),
                        user.getDefaultRefreshTokenDuration(), TimeUnit.MILLISECONDS, idTokenRequest,
                        keyPairData.getId(), keyPairData.getPrivateKey());
                Tokens tokens = new Tokens(accessToken, refreshToken, TokenType.BEARER,
                        user.getDefaultAccessTokenDuration() / 1000L, user.getDefaultRefreshTokenDuration() / 1000L, idToken);
                return Optional.of(tokens);
            } else {
                LOG.info("User {} not found", context.getUserId());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean setScope(Code code, Scope scope) {
        return codeCache.setScope(code, scope);
    }

    @Override
    public boolean revoke(OrganizationId organizationId, ProjectId projectId, RevokeTokenRequest request) {
        JWToken token = request.getToken();
        DefaultClaims defaultClaims = TokenUtils.extractClaims(token);
        String subject = defaultClaims.getSubject();
        Optional<User> userOptional = modelCache.getUser(organizationId, projectId, UserId.from(subject));
        if (userOptional.isPresent()) {
            Optional<Jws<Claims>> claimsJws = TokenUtils.verify(token, userOptional.get().getCertificate().getPublicKey());
            LOG.info("JWT verified={}", claimsJws.isPresent());
            if (claimsJws.isPresent()) {
                tokenCache.addRevokedToken(token);
                return true;
            }
        } else {
            ClientId clientId = ClientId.from(defaultClaims.getSubject());
            Optional<Client> clientOptional = this.modelCache.getClient(organizationId, projectId, clientId);
            Optional<Project> projectOptional = this.modelCache.getProject(organizationId, projectId);
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

    @Override
    public Optional<UserInfoResponse> getUserInfo(OrganizationId organizationId, ProjectId projectId, JWToken token) {
        if (!tokenCache.isRevoked(token)) {
            DefaultClaims defaultClaims = TokenUtils.extractClaims(token);
            String subject = defaultClaims.getSubject();
            Optional<User> userOptional = modelCache.getUser(organizationId, projectId, UserId.from(subject));
            if (userOptional.isPresent()) {
                PublicKey publicKey = userOptional.get().getKeyPairData().getPublicKey();
                Optional<Jws<Claims>> claims = TokenUtils.verify(token, publicKey);
                String type = claims.get().getBody().get(TYPE_CLAIM, String.class);
                if (claims.isPresent() &&
                        claims.get().getBody().getSubject().equals(userOptional.get().getId().getId()) &&
                        TokenType.BEARER.getType().equals(type)) {
                    return Optional.of(new UserInfoResponse(userOptional.get().getId().getId()));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthorizationCodeContext> getAuthorizationCodeContext(Code code) {
        return codeCache.get(code);
    }

}
