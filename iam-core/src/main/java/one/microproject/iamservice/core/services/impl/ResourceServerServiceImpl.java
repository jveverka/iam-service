package one.microproject.iamservice.core.services.impl;

import io.jsonwebtoken.impl.DefaultClaims;
import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.caches.TokenCache;
import one.microproject.iamservice.core.utils.TokenUtils;
import one.microproject.iamservice.core.services.ResourceServerService;
import one.microproject.iamservice.core.dto.IntrospectRequest;
import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.services.dto.ClientInfo;
import one.microproject.iamservice.core.services.dto.UserInfo;
import one.microproject.iamservice.core.services.dto.ProjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateEncodingException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceServerServiceImpl implements ResourceServerService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServerServiceImpl.class);

    private final ModelCache modelCache;
    private final TokenCache tokenCache;
    private final TokenValidator tokenValidator;

    public ResourceServerServiceImpl(ModelCache modelCache, TokenCache tokenCache, TokenValidator tokenValidator) {
        this.modelCache = modelCache;
        this.tokenCache = tokenCache;
        this.tokenValidator = tokenValidator;
    }

    @Override
    public IntrospectResponse introspect(OrganizationId organizationId, ProjectId projectId, IntrospectRequest request) {
        boolean isRevoked = this.tokenCache.isRevoked(request.getToken());
        if (!isRevoked) {
            try {
                DefaultClaims defaultClaims = TokenUtils.extractClaims(request.getToken());
                UserId userId = UserId.from(defaultClaims.getSubject());
                Optional<User> userOptional = this.modelCache.getUser(organizationId, projectId, userId);
                if (userOptional.isPresent()) {
                    Optional<StandardTokenClaims> tokenClaims = tokenValidator.validateToken(userOptional.get().getCertificate().getPublicKey(), request.getToken());
                    LOG.info("JWT verified={}", tokenClaims.isPresent());
                    return new IntrospectResponse(tokenClaims.isPresent());
                } else {
                    ClientId clientId = ClientId.from(defaultClaims.getSubject());
                    Optional<Client> clientOptional = this.modelCache.getClient(organizationId, projectId, clientId);
                    Optional<Project> projectOptional = this.modelCache.getProject(organizationId, projectId);
                    if (projectOptional.isPresent() && clientOptional.isPresent()) {
                        Optional<StandardTokenClaims> tokenClaims = tokenValidator.validateToken(projectOptional.get().getCertificate().getPublicKey(), request.getToken());
                        LOG.info("JWT verified={}", tokenClaims.isPresent());
                        return new IntrospectResponse(tokenClaims.isPresent());
                    }
                    LOG.info("JWT subject {} not found", userId);
                }
            } catch (Exception e) {
                LOG.error("JWT introspection failed: ", e);
            }
        } else {
            LOG.info("JWT is revoked: {}", request.getToken());
        }
        return new IntrospectResponse(false);
    }

    @Override
    public Optional<ProjectInfo> getProjectInfo(OrganizationId organizationId, ProjectId projectId) throws CertificateEncodingException {
        Optional<Organization> organizationOptional = modelCache.getOrganization(organizationId);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                Set<String> userIds = modelCache.getUsers(organizationId, projectId).stream().map(user -> user.getId().getId()).collect(Collectors.toSet());
                Set<String> clientIds = project.getClients().stream().map(c -> c.getId()).collect(Collectors.toSet());
                ProjectInfo projectInfo = new ProjectInfo(project.getId().getId(), project.getOrganizationId().getId(),
                        project.getName(), organizationOptional.get().getKeyPairData(), project.getKeyPairData(), clientIds, userIds);
                return Optional.of(projectInfo);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserInfo> getUserInfo(OrganizationId organizationId, ProjectId projectId, UserId userId) throws CertificateEncodingException {
        Optional<Organization> organizationOptional = modelCache.getOrganization(organizationId);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
            if (projectOptional.isPresent()) {
                Optional<User> userOptional = modelCache.getUser(organizationId, projectId, userId);
                Set<String> permissions = modelCache.getPermissions(organizationId, projectId, userId).stream().map(p->p.asStringValue()).collect(Collectors.toSet());
                if (userOptional.isPresent()) {
                    Set<String> roles = userOptional.get().getRoles().stream().map(r->r.getId()).collect(Collectors.toSet());
                    UserInfo userInfo = new UserInfo(userId.getId(), projectId.getId(), organizationId.getId(),
                            userOptional.get().getName(), organizationOptional.get().getKeyPairData(),
                            projectOptional.get().getKeyPairData(), userOptional.get().getKeyPairData(),
                            roles, permissions);
                    return Optional.of(userInfo);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ClientInfo> getClientInfo(OrganizationId organizationId, ProjectId projectId, ClientId clientId) throws CertificateEncodingException {
        Optional<Organization> organizationOptional = modelCache.getOrganization(organizationId);
        if (organizationOptional.isPresent()) {
            Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
            if (projectOptional.isPresent()) {
                Optional<Client> clientOptional = modelCache.getClient(organizationId, projectId, clientId);
                Set<String> permissions = modelCache.getPermissions(organizationId, projectId, clientId).stream().map(p->p.asStringValue()).collect(Collectors.toSet());
                if (clientOptional.isPresent()) {
                    Set<String> roles = clientOptional.get().getRoles().stream().map(r->r.getId()).collect(Collectors.toSet());
                    ClientInfo clientInfo = new ClientInfo(clientId.getId(), clientOptional.get().getName(), roles, permissions);
                    return Optional.of(clientInfo);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Project> getProject(OrganizationId organizationId, ProjectId projectId) {
        return modelCache.getProject(organizationId, projectId);
    }

    @Override
    public Optional<User> getUser(OrganizationId organizationId, ProjectId projectId, UserId userId) {
        return modelCache.getUser(organizationId, projectId, userId);
    }

    @Override
    public Optional<Organization> getOrganization(OrganizationId organizationId) {
        return modelCache.getOrganization(organizationId);
    }

}
