package one.microproject.iamservice.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.microproject.iamservice.core.dto.CreateUser;
import one.microproject.iamservice.core.dto.UserCredentialsChangeRequest;
import one.microproject.iamservice.core.model.Credentials;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPCredentials;
import one.microproject.iamservice.core.services.admin.UserManagerService;
import one.microproject.iamservice.core.services.dto.CreateUserRequest;
import one.microproject.iamservice.server.services.IAMSecurityValidator;
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
@Tag(name = "Project User Management", description = "APIs for managing Project's Users.")
public class ProjectUserManagementController {

    private final UserManagerService userManagerService;
    private final IAMSecurityValidator iamSecurityValidator;

    public ProjectUserManagementController(UserManagerService userManagerService,
                                           IAMSecurityValidator iamSecurityValidator) {
        this.userManagerService = userManagerService;
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @Operation(description = "Create new project user.")
    @PostMapping("/{organization-id}/{project-id}/users")
    public ResponseEntity<Void> createUser(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @RequestBody CreateUser createUser) throws PKIException  {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        UserId userId = UserId.from(createUser.getId());
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        CreateUserRequest request = new CreateUserRequest(userId, createUser.getName(), createUser.getDefaultAccessTokenDuration(), createUser.getDefaultRefreshTokenDuration(),
                createUser.getEmail(), createUser.getUserProperties());
        Optional<User> userOptional = userManagerService.create(orgId, projId, request);
        if (userOptional.isPresent()) {
            UPCredentials upCredentials = new UPCredentials(userId, createUser.getPassword());
            userManagerService.setCredentials(orgId, projId, userId, upCredentials);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(description = "Delete project user.")
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

    @Operation(description = "Change user's password.")
    @PutMapping("/{organization-id}/{project-id}/users/{user-id}/change-password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("user-id") String userId,
                                                   @RequestBody UserCredentialsChangeRequest userCredentialsChangeRequest) throws PKIException {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        UserId uId = UserId.from(userId);
        iamSecurityValidator.verifyUserAccess(orgId, projId, uId);
        Credentials credentials = new UPCredentials(uId, userCredentialsChangeRequest.getNewPassword());
        userManagerService.setCredentials(orgId, projId, uId, credentials);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(description = "Add existing role to existing user.")
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

    @Operation(description = "Remove role from user.")
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
