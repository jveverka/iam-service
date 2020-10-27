package itx.iamservice.server.controller;

import itx.iamservice.core.dto.CreateUser;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.dto.CreateUserRequest;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
                                           @PathVariable("project-id") String projectId,
                                           @RequestBody CreateUser createUser) throws PKIException  {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        CreateUserRequest request = new CreateUserRequest(UserId.from(createUser.getId()), createUser.getName(), createUser.getDefaultAccessTokenDuration(), createUser.getDefaultRefreshTokenDuration(), createUser.getEmail());
        Optional<User> userOptional = userManagerService.create(orgId, projId, request);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{organization-id}/{project-id}/users/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @PathVariable("user-id") String userId) {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        if (userManagerService.remove(orgId, projId, UserId.from(userId))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{organization-id}/{project-id}/users/{user-id}/change-password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("user-id") String userId) {
        //TODO: add missing implementation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{organization-id}/{project-id}/users/{user-id}/roles/{role-id}")
    public ResponseEntity<Void> addRoleToUser(@PathVariable("organization-id") String organizationId,
                                              @PathVariable("project-id") String projectId,
                                              @PathVariable("user-id") String userId,
                                              @PathVariable("role-id") String roleId) {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        if (userManagerService.assignRole(orgId, projId, UserId.from(userId), RoleId.from(roleId))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{organization-id}/{project-id}/users/{user-id}/roles/{role-id}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("user-id") String userId,
                                                   @PathVariable("role-id") String roleId) {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        if (userManagerService.removeRole(orgId, projId, UserId.from(userId), RoleId.from(roleId))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
