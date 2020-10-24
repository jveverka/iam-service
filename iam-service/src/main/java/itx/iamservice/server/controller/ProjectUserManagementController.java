package itx.iamservice.server.controller;

import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/services/management")
public class ProjectUserManagementController {

    private final UserManagerService userManagerService;
    private final IAMSecurityValidator iamSecurityValidator;

    public ProjectUserManagementController(UserManagerService userManagerService,
                                           IAMSecurityValidator iamSecurityValidator) {
        this.userManagerService = userManagerService;
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @PostMapping("/{organization-id}/{project-id}/users")
    public ResponseEntity<Void> createUser(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId) {
        //TODO: add missing implementation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{organization-id}/{project-id}/users/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @PathVariable("user-id") String userId) {
        //TODO: add missing implementation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{organization-id}/{project-id}/users/{user-id}/change-password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("user-id") String userId) {
        //TODO: add missing implementation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
