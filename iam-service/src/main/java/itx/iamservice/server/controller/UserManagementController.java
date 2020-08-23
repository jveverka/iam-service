package itx.iamservice.server.controller;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.dto.CreateUserRequest;
import itx.iamservice.core.services.dto.SetUserNamePasswordCredentialsRequest;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static itx.iamservice.core.ModelCommons.ADMIN_PROJECT_SET;
import static itx.iamservice.core.ModelCommons.getProjectAdminPermissionSet;

@RestController
@RequestMapping(path = "/services/management")
public class UserManagementController {

    private final UserManagerService userManagerService;
    private final IAMSecurityValidator iamSecurityValidator;

    public UserManagementController(@Autowired UserManagerService userManagerService,
                                    @Autowired IAMSecurityValidator iamSecurityValidator) {
        this.userManagerService = userManagerService;
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @PostMapping(path = "/{organization-id}/projects/{project-id}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserId> createUser(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @RequestBody CreateUserRequest createUserRequest,
                                             @RequestHeader("Authorization") String authorization) throws PKIException {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        iamSecurityValidator.validate(OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        Optional<User> user = userManagerService.create(OrganizationId.from(organizationId), ProjectId.from(projectId), createUserRequest);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getId());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteUser(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @PathVariable("user-id") String userId,
                                           @RequestHeader("Authorization") String authorization) {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        iamSecurityValidator.validate(OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        boolean result = userManagerService.remove(OrganizationId.from(organizationId), ProjectId.from(projectId), UserId.from(userId));
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignRoleToUser(@PathVariable("organization-id") String organizationId,
                                                 @PathVariable("project-id") String projectId,
                                                 @PathVariable("user-id") String userId,
                                                 @PathVariable("role-id") String roleId,
                                                 @RequestHeader("Authorization") String authorization) {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        iamSecurityValidator.validate(OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        boolean result = userManagerService.assignRole(OrganizationId.from(organizationId), ProjectId.from(projectId), UserId.from(userId), RoleId.from(roleId));
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RoleId>> getRolesForUser(@PathVariable("organization-id") String organizationId,
                                                              @PathVariable("project-id") String projectId,
                                                              @PathVariable("user-id") String userId,
                                                              @RequestHeader("Authorization") String authorization) {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        iamSecurityValidator.validate(OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        Set<RoleId> roles = userManagerService.getRoles(OrganizationId.from(organizationId), ProjectId.from(projectId), UserId.from(userId));
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("user-id") String userId,
                                                   @PathVariable("role-id") String roleId,
                                                   @RequestHeader("Authorization") String authorization) {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        iamSecurityValidator.validate(OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        boolean result = userManagerService.removeRole(OrganizationId.from(organizationId), ProjectId.from(projectId), UserId.from(userId), RoleId.from(roleId));
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}/credentials-username-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> setUsernamePasswordCredentials(@PathVariable("organization-id") String organizationId,
                                                               @PathVariable("project-id") String projectId,
                                                               @PathVariable("user-id") String userId,
                                                               @RequestBody SetUserNamePasswordCredentialsRequest setUserNamePasswordCredentialsRequest,
                                                               @RequestHeader("Authorization") String authorization) throws PKIException {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        iamSecurityValidator.validate(OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        UserId id = UserId.from(setUserNamePasswordCredentialsRequest.getUserName());
        UPCredentials credentials = new UPCredentials(id, setUserNamePasswordCredentialsRequest.getPassword());
        boolean result = userManagerService.setCredentials(OrganizationId.from(organizationId), ProjectId.from(projectId), UserId.from(userId), credentials);
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
