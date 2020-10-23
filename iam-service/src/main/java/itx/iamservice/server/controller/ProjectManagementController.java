package itx.iamservice.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import itx.iamservice.core.services.admin.ProjectManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/services/management")
@Tag(name = "Management", description = "APIs providing self-service user management.")
public class ProjectManagementController {

    private final ProjectManagerService projectManagerService;

    public ProjectManagementController(ProjectManagerService projectManagerService) {
        this.projectManagerService = projectManagerService;
    }

    @PutMapping("/{organization-id}/{project-id}")
    public ResponseEntity<Void> modifyProject(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId) {
        //TODO: add missing implementation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
