package itx.iamservice.core.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.model.utils.TokenUtils;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.dto.IntrospectRequest;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.services.dto.ClientInfo;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
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

    public ResourceServerServiceImpl(ModelCache modelCache, TokenCache tokenCache) {
        this.modelCache = modelCache;
        this.tokenCache = tokenCache;
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
                    Optional<Jws<Claims>> claimsJws = TokenUtils.verify(request.getToken(), userOptional.get().getCertificate().getPublicKey());
                    LOG.info("JWT verified={}", claimsJws.isPresent());
                    return new IntrospectResponse(claimsJws.isPresent());
                } else {
                    ClientId clientId = ClientId.from(defaultClaims.getSubject());
                    Optional<Client> clientOptional = this.modelCache.getClient(organizationId, projectId, clientId);
                    Optional<Project> projectOptional = this.modelCache.getProject(organizationId, projectId);
                    if (projectOptional.isPresent() && clientOptional.isPresent()) {
                        Optional<Jws<Claims>> claimsJws = TokenUtils.verify(request.getToken(), projectOptional.get().getCertificate().getPublicKey());
                        LOG.info("JWT verified={}", claimsJws.isPresent());
                        return new IntrospectResponse(claimsJws.isPresent());
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
                Set<UserId> userIds = modelCache.getUsers(organizationId, projectId).stream().map(user -> user.getId()).collect(Collectors.toSet());
                ProjectInfo projectInfo = new ProjectInfo(project.getId(), project.getOrganizationId(),
                        project.getName(), organizationOptional.get().getKeyPairData(), project.getKeyPairData(), project.getClients(), userIds);
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
                Set<Permission> permissions = modelCache.getPermissions(organizationId, projectId, userId);
                if (userOptional.isPresent()) {
                    UserInfo userInfo = new UserInfo(userId, projectId, organizationId,
                            userOptional.get().getName(), organizationOptional.get().getKeyPairData(),
                            projectOptional.get().getKeyPairData(), userOptional.get().getKeyPairData(),
                            userOptional.get().getRoles(), permissions);
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
                Set<Permission> permissions = modelCache.getPermissions(organizationId, projectId, clientId);
                if (clientOptional.isPresent()) {
                    ClientInfo clientInfo = new ClientInfo(clientId, clientOptional.get().getName(), clientOptional.get().getRoles(), permissions);
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
