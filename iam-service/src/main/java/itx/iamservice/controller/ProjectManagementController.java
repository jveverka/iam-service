package itx.iamservice.controller;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.services.dto.CreateOrganizationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = "/services/management")
public class ProjectManagementController {

    private final ProjectManagerService projectManagerService;

    public ProjectManagementController(@Autowired ProjectManagerService projectManagerService) {
        this.projectManagerService = projectManagerService;
    }

    @PostMapping(path = "/{organization-id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectId> createProject(@PathVariable("organization-id") String organizationId,
                                                   @RequestBody CreateOrganizationRequest request) throws PKIException {
        OrganizationId id = OrganizationId.from(organizationId);
        Optional<ProjectId> projectId = projectManagerService.create(id, request.getName());
        return ResponseEntity.of(projectId);
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProject(@PathVariable("organization-id") String organizationId,
                                              @PathVariable("project-id") String projectId) {
        projectManagerService.remove(OrganizationId.from(organizationId), ProjectId.from(projectId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
