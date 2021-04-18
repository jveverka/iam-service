package one.microproject.iamservice.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.ResourceServerService;
import one.microproject.iamservice.core.services.admin.OrganizationManagerService;
import one.microproject.iamservice.core.services.dto.ClientInfo;
import one.microproject.iamservice.core.services.dto.OrganizationInfo;
import one.microproject.iamservice.core.services.dto.ProjectInfo;
import one.microproject.iamservice.core.services.dto.UserInfo;
import one.microproject.iamservice.core.dto.BuildInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/services/discovery")
@Tag(name = "Discovery", description = "APIs providing public data about Organizations and Projects.")
public class DiscoveryController {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryController.class);

    private final OrganizationManagerService organizationManagerService;
    private final ResourceServerService resourceServerService;
    private final BuildProperties buildProperties;
    private final String appInstanceId;

    public DiscoveryController(@Autowired OrganizationManagerService organizationManagerService,
                               @Autowired ResourceServerService resourceServerService,
                               @Autowired(required = false) BuildProperties buildProperties) {
        this.organizationManagerService = organizationManagerService;
        this.resourceServerService = resourceServerService;
        this.buildProperties = buildProperties;
        this.appInstanceId = UUID.randomUUID().toString();
        LOG.info("IAM APP ID: {}", appInstanceId);
    }

    @GetMapping("/build-info")
    public ResponseEntity<BuildInfo> getBuildInfo() {
        if (buildProperties != null) {
            return ResponseEntity.ok(new BuildInfo(appInstanceId,
                    buildProperties.getGroup(), buildProperties.getArtifact(), buildProperties.getName(), buildProperties.getVersion()));
        } else {
            return ResponseEntity.ok(new BuildInfo(appInstanceId));
        }
    }

    @Operation(description = "Get list of all organizations managed by this service.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<OrganizationInfo>> getOrganizationsInfo() throws CertificateEncodingException {
        Collection<OrganizationInfo> info = organizationManagerService.getAllInfo();
        return ResponseEntity.ok(info);
    }

    @Operation(description = "Get single organization info by organization-id.")
    @GetMapping(path = "/{organization-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrganizationInfo> getOrganizationInfo(@PathVariable("organization-id") String organizationId) throws CertificateEncodingException {
        Optional<OrganizationInfo> info = organizationManagerService.getInfo(OrganizationId.from(organizationId));
        return ResponseEntity.of(info);
    }

    @Operation(description = "Get single project info by organization-id and project-id.")
    @GetMapping(path = "/{organization-id}/{project-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectInfo> getProject(@PathVariable("organization-id") String organizationId,
                                                  @PathVariable("project-id") String projectId) throws CertificateEncodingException {
        Optional<Organization> organizationOptional = organizationManagerService.get(OrganizationId.from(organizationId));
        if(organizationOptional.isPresent()) {
            Optional<ProjectInfo> projectInfo = resourceServerService.getProjectInfo(OrganizationId.from(organizationId), ProjectId.from(projectId));
            return ResponseEntity.of(projectInfo);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(description = "Get single User info by organization-id, project-id and user-id.")
    @GetMapping(path = "/{organization-id}/{project-id}/users/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfo> getUsers(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @PathVariable("user-id") String userId) throws CertificateEncodingException {
        Optional<Organization> organizationOptional = organizationManagerService.get(OrganizationId.from(organizationId));
        if(organizationOptional.isPresent()) {
            Optional<UserInfo> userInfo = resourceServerService.getUserInfo(OrganizationId.from(organizationId), ProjectId.from(projectId), UserId.from(userId));
            return ResponseEntity.of(userInfo);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(description = "Get single Client info by organization-id, project-id and client-id.")
    @GetMapping(path = "/{organization-id}/{project-id}/clients/{client-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientInfo> getClient(@PathVariable("organization-id") String organizationId,
                                                @PathVariable("project-id") String projectId,
                                                @PathVariable("client-id") String clientId) throws CertificateEncodingException {
        Optional<Organization> organizationOptional = organizationManagerService.get(OrganizationId.from(organizationId));
        if(organizationOptional.isPresent()) {
            Optional<ClientInfo> clientInfo = resourceServerService.getClientInfo(OrganizationId.from(organizationId), ProjectId.from(projectId), ClientId.from(clientId));
            return ResponseEntity.of(clientInfo);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
