package itx.iamservice.core.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
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
import itx.iamservice.core.services.dto.IntrospectRequest;
import itx.iamservice.core.services.dto.IntrospectResponse;
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
                Set<UserId> userIds = projectOptional.get().getUsers().stream().map(user -> user.getId()).collect(Collectors.toSet());
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
                Optional<User> userOptional = projectOptional.get().getUser(userId);
                if (userOptional.isPresent()) {
                    UserInfo userInfo = new UserInfo(userId, projectId, organizationId,
                            userOptional.get().getName(), organizationOptional.get().getKeyPairData(),
                            projectOptional.get().getKeyPairData(), userOptional.get().getKeyPairData(),
                            userOptional.get().getRoles());
                    return Optional.of(userInfo);
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
        Optional<Project> projectOptional = modelCache.getProject(organizationId, projectId);
        if (projectOptional.isPresent()) {
            return projectOptional.get().getUser(userId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Organization> getOrganization(OrganizationId organizationId) {
        return modelCache.getOrganization(organizationId);
    }

}
