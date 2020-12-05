package one.microproject.iamservice.core.services.impl;

import io.jsonwebtoken.impl.DefaultClaims;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.Credentials;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPCredentials;
import one.microproject.iamservice.core.model.utils.TokenUtils;
import one.microproject.iamservice.core.services.AuthenticationService;
import one.microproject.iamservice.core.services.TokenGenerator;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.services.caches.AuthorizationCodeCache;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.caches.TokenCache;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.services.dto.IdTokenRequest;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.services.dto.RevokeTokenRequest;
import one.microproject.iamservice.core.services.dto.Scope;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.services.dto.UserInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.security.PublicKey;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final ModelCache modelCache;
    private final TokenCache tokenCache;
    private final AuthorizationCodeCache codeCache;
    private final TokenGenerator tokenGenerator;
    private final TokenValidator tokenValidator;

    public AuthenticationServiceImpl(ModelCache modelCache, TokenCache tokenCache, AuthorizationCodeCache codeCache,
                                     TokenGenerator tokenGenerator, TokenValidator tokenValidator) {
        this.modelCache = modelCache;
        this.tokenCache = tokenCache;
        this.codeCache = codeCache;
        this.tokenGenerator = tokenGenerator;
        this.tokenValidator = tokenValidator;
    }

    @Override
    public Optional<TokenResponse> authenticate(URI issuerUri, OrganizationId organizationId, ProjectId projectId,
                                                ClientCredentials cc, Scope scope,
                                                UPAuthenticationRequest authenticationRequest,
                                                IdTokenRequest idTokenRequest) {
        Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> optionalClient = modelCache.getClient(organizationId, projectId, cc.getId());
            if (optionalClient.isPresent() && Boolean.FALSE.equals(optionalClient.get().getProperties().getPasswordCredentialsEnabled())) {
                LOG.info("Invalid flow for client {} !", optionalClient.get().getId());
                return Optional.empty();
            }
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
                    return Optional.of(tokenGenerator.generate(issuerUri, organizationId, projectOptional.get(), user,
                            userPermissions, authenticationRequest.getScope(), authenticationRequest.getClientCredentials().getId() , idTokenRequest));
                }
            }
        } else {
            LOG.info("JWT subject {} not found", authenticationRequest.getUserId());
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenResponse> authenticate(URI issuerUri, OrganizationId organizationId, ProjectId projectId, ClientCredentials clientCredentials,
                                                Scope scope, IdTokenRequest idTokenRequest) {
        Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> clientOptional = modelCache.getClient(organizationId, projectId, clientCredentials.getId());
            if (clientOptional.isPresent()) {
                if (Boolean.FALSE.equals(clientOptional.get().getProperties().getClientCredentialsEnabled())) {
                    LOG.info("Invalid flow for client {} !", clientOptional.get().getId());
                    return Optional.empty();
                }
                boolean validationResult = modelCache.verifyClientCredentials(organizationId, projectId, clientCredentials);
                if (validationResult) {
                    Client client = clientOptional.get();
                    Project project = projectOptional.get();
                    Set<Permission> clientPermissions = modelCache.getPermissions(organizationId, projectId, client.getId());
                    return Optional.of(tokenGenerator.generate(issuerUri, organizationId, project, clientPermissions, client, scope, idTokenRequest));
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
    public Optional<TokenResponse> refreshTokens(OrganizationId organizationId, ProjectId projectId, JWToken token,
                                                 ClientCredentials clientCredentials, Scope scope, IdTokenRequest idTokenRequest) {
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
                Optional<StandardTokenClaims> claimsOptional = tokenValidator.validateToken(user.getCertificate().getPublicKey(), token);
                LOG.info("JWT verified={}", claimsOptional.isPresent());
                if (claimsOptional.isPresent()) {
                    try {
                        StandardTokenClaims tokenClaims = claimsOptional.get();
                        if (TokenType.REFRESH.equals(tokenClaims.getType())) {
                            Set<Permission> userPermissions = modelCache.getPermissions(organizationId, projectId, user.getId());
                            return Optional.of(tokenGenerator.generate(tokenClaims.getIssuerUri(), organizationId, projectOptional.get(), user, userPermissions, scope, clientCredentials.getId(), idTokenRequest));
                        } else {
                            LOG.info("Invalid JWT type {}, expected type {}", tokenClaims.getType(), TokenType.BEARER.getType());
                        }
                    } catch (Exception e) {
                        LOG.warn("Exception: {}", e.getMessage());
                    }
                } else {
                    LOG.warn("JWT is invalid !");
                }
            } else {
                Optional<Client> clientOptional = modelCache.getClient(organizationId, projectId, clientCredentials.getId());
                if (clientOptional.isPresent()) {
                    boolean validationResult = modelCache.verifyClientCredentials(organizationId, projectId, clientCredentials);
                    if (validationResult) {
                        try {
                            Client client = clientOptional.get();
                            Project project = projectOptional.get();
                            Optional<StandardTokenClaims> claimsOptional = tokenValidator.validateToken(projectOptional.get().getCertificate().getPublicKey(), token);
                            LOG.info("JWT verified={}", claimsOptional.isPresent());
                            if (claimsOptional.isPresent()) {
                                StandardTokenClaims tokenClaims = claimsOptional.get();
                                Set<Permission> clientPermissions = modelCache.getPermissions(organizationId, projectId, client.getId());
                                return Optional.of(tokenGenerator.generate(tokenClaims.getIssuerUri(), organizationId, project, clientPermissions, client, scope, idTokenRequest));
                            } else {
                                LOG.warn("JWT is invalid !");
                            }
                        } catch (Exception e) {
                            LOG.warn("Exception: {}", e.getMessage());
                        }
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
    public Optional<TokenResponse> authenticate(Code code, IdTokenRequest idTokenRequest) {
        Optional<AuthorizationCodeContext> contextOptional = codeCache.get(code);
        if (contextOptional.isPresent()) {
            AuthorizationCodeContext context = contextOptional.get();
            Optional<Client> optionalClient = modelCache.getClient(context.getOrganizationId(), context.getProjectId(), context.getClientId());
            if (optionalClient.isPresent() && Boolean.FALSE.equals(optionalClient.get().getProperties().getAuthorizationCodeGrantEnabled())) {
                LOG.info("Invalid flow for clientId {} !", optionalClient.get().getId());
                return Optional.empty();
            }
            Optional<User> optionalUser = modelCache.getUser(context.getOrganizationId(), context.getProjectId(), context.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                return Optional.of(tokenGenerator.generate(context, user, idTokenRequest));
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
    public Optional<AuthorizationCode> login(URI issuerUri, OrganizationId organizationId, ProjectId projectId, UserId userId, ClientId clientId, String password, Scope scope, String state, String redirectURI) {
        Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            Optional<Client> optionalClient = modelCache.getClient(organizationId, projectId, clientId);
            if (!optionalClient.isPresent()) {
                LOG.info("Invalid clientId {} !", clientId);
                return Optional.empty();
            } else {
                if (Boolean.FALSE.equals(optionalClient.get().getProperties().getAuthorizationCodeGrantEnabled())) {
                    LOG.info("Invalid flow for clientId {} !", clientId);
                    return Optional.empty();
                }
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

                    Code code = Code.from(UUID.randomUUID().toString());
                    AuthorizationCodeContext authorizationCodeContext =
                            new AuthorizationCodeContext(issuerUri, organizationId, projectId, clientId, userId, state, new Date(), filteredScopes, projectOptional.get().getAudience(), redirectURI);
                    AuthorizationCode authorizationCode = codeCache.save(code, authorizationCodeContext);
                    return Optional.of(authorizationCode);
                }
            }
        } else {
            LOG.info("JWT subject {} not found", userId);
        }
        return Optional.empty();
    }

    @Override
    public boolean revoke(OrganizationId organizationId, ProjectId projectId, RevokeTokenRequest request) {
        JWToken token = request.getToken();
        DefaultClaims defaultClaims = TokenUtils.extractClaims(token);
        String subject = defaultClaims.getSubject();
        Optional<User> userOptional = modelCache.getUser(organizationId, projectId, UserId.from(subject));
        if (userOptional.isPresent()) {
            Optional<StandardTokenClaims> claimsOptional = tokenValidator.validateToken(userOptional.get().getCertificate().getPublicKey(), token);
            LOG.info("JWT verified={}", claimsOptional.isPresent());
            if (claimsOptional.isPresent()) {
                tokenCache.addRevokedToken(token);
                return true;
            }
        } else {
            ClientId clientId = ClientId.from(defaultClaims.getSubject());
            Optional<Client> clientOptional = this.modelCache.getClient(organizationId, projectId, clientId);
            Optional<Project> projectOptional = this.modelCache.getProject(organizationId, projectId);
            if (projectOptional.isPresent() && clientOptional.isPresent()) {
                Optional<StandardTokenClaims> claimsOptional = tokenValidator.validateToken(projectOptional.get().getCertificate().getPublicKey(), token);
                LOG.info("JWT verified={}", claimsOptional.isPresent());
                if (claimsOptional.isPresent()) {
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
                Optional<StandardTokenClaims> claimsOptional = tokenValidator.validateToken(publicKey, token);
                if (claimsOptional.isPresent()) {
                    StandardTokenClaims tokenClaims = claimsOptional.get();
                    if (tokenClaims.getSubject().equals(userOptional.get().getId().getId()) &&
                            TokenType.BEARER.equals(tokenClaims.getType())) {
                        return Optional.of(new UserInfoResponse(userOptional.get().getId().getId()));
                    }
                }
            }
        }
        return Optional.empty();
    }

}
