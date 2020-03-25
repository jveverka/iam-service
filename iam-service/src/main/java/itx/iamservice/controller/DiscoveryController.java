package itx.iamservice.controller;

import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping(path = "/services/discovery")
public class DiscoveryController {

    private final OrganizationManagerService organizationManagerService;
    private final ResourceServerService resourceServerService;

    public DiscoveryController(@Autowired OrganizationManagerService organizationManagerService,
                               @Autowired ResourceServerService resourceServerService) {
        this.organizationManagerService = organizationManagerService;
        this.resourceServerService = resourceServerService;
    }

    @GetMapping(path = "/organizations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<OrganizationInfo>> getOrganizationsInfo() throws CertificateEncodingException {
        Collection<OrganizationInfo> info = organizationManagerService.getAllInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping(path = "/organizations/{organization-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrganizationInfo> getOrganizationInfo(@PathVariable("organization-id") String organizationId) throws CertificateEncodingException {
        Optional<OrganizationInfo> info = organizationManagerService.getInfo(OrganizationId.from(organizationId));
        return ResponseEntity.of(info);
    }

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

    @GetMapping(path = "/{organization-id}/{project-id}/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfo> getUser(@PathVariable("organization-id") String organizationId,
                                            @PathVariable("project-id") String projectId,
                                            @PathVariable("user-id") String userId) throws CertificateEncodingException {
        Optional<Organization> organizationOptional = organizationManagerService.get(OrganizationId.from(organizationId));
        if(organizationOptional.isPresent()) {
            Optional<UserInfo> userInfo = resourceServerService.getUserInfo(OrganizationId.from(organizationId), ProjectId.from(projectId), UserId.from(userId));
            return ResponseEntity.of(userInfo);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
