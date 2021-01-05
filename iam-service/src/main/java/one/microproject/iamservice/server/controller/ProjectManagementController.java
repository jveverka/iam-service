package one.microproject.iamservice.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.microproject.iamservice.core.dto.CreateRole;
import one.microproject.iamservice.core.dto.PermissionInfo;
import one.microproject.iamservice.core.dto.RoleInfo;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.PermissionParsingException;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.admin.ProjectManagerService;
import one.microproject.iamservice.core.services.dto.CreateRoleRequest;
import one.microproject.iamservice.server.services.IAMSecurityValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping(path = "/services/management")
@Tag(name = "Project Management", description = "APIs for managing Project setup.")
public class ProjectManagementController {

    private final ProjectManagerService projectManagerService;
    private final IAMSecurityValidator iamSecurityValidator;

    public ProjectManagementController(ProjectManagerService projectManagerService,
                                       IAMSecurityValidator iamSecurityValidator) {
        this.projectManagerService = projectManagerService;
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @Operation(description = "Create new project role.")
    @PostMapping("/{organization-id}/{project-id}/roles")
    public ResponseEntity<Void> createRole(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @RequestBody CreateRole createRole) {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        RoleId roleId = RoleId.from(createRole.getId());
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        projectManagerService.addRole(orgId, projId, new CreateRoleRequest(roleId, createRole.getName()));
        for (PermissionInfo permissionInfo: createRole.getPermissions()) {
            Permission permission = new Permission(permissionInfo.getService(), permissionInfo.getResource(), permissionInfo.getAction());
            projectManagerService.addPermission(orgId, projId, permission);
            projectManagerService.addPermissionToRole(orgId, projId, roleId, permission.getId());
        }
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Get all roles with permissions available for this project.")
    @GetMapping("/{organization-id}/{project-id}/roles")
    public ResponseEntity<Collection<RoleInfo>> getRoles(@PathVariable("organization-id") String organizationId,
                                                         @PathVariable("project-id") String projectId) {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        List<RoleInfo> roles = new ArrayList<>();
        for (Role role: projectManagerService.getRoles(orgId, projId)) {
            Set<PermissionInfo> permissionInfo = new HashSet<>();
            for (Permission permission: role.getPermissions()) {
                permissionInfo.add(new PermissionInfo(permission.getService(), permission.getResource(), permission.getAction()));
            }
            roles.add(new RoleInfo(role.getId().getId(), role.getName(), permissionInfo));
        }
        return ResponseEntity.ok(roles);
    }

    @Operation(description = "Get all permissions available for this project.")
    @GetMapping("/{organization-id}/{project-id}/permissions")
    public ResponseEntity<Set<PermissionInfo>> getPermissions(@PathVariable("organization-id") String organizationId,
                                                              @PathVariable("project-id") String projectId) {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        Set<PermissionInfo> permissionInfo = new HashSet<>();
        for (Permission permission: projectManagerService.getPermissions(orgId, projId)) {
            permissionInfo.add(new PermissionInfo(permission.getService(), permission.getResource(), permission.getAction()));
        }
        return ResponseEntity.ok(permissionInfo);
    }

    @Operation(description = "Remove permission from the project.")
    @DeleteMapping("/{organization-id}/{project-id}/permissions/{permission-id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("organization-id") String organizationId,
                                                 @PathVariable("project-id") String projectId,
                                                 @PathVariable("permission-id") String permissionId) throws PermissionParsingException {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        Permission permission = Permission.from(permissionId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        if (projectManagerService.removePermission(orgId, projId, permission.getId())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(description = "Remove role from the project.")
    @DeleteMapping("/{organization-id}/{project-id}/roles/{role-id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @PathVariable("role-id") String roleId) {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        RoleId rId = RoleId.from(roleId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        if (projectManagerService.removeRole(orgId, projId, rId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(description = "Set audience for project.")
    @PutMapping("/{organization-id}/{project-id}/audience")
    public ResponseEntity<Void> setAudience(@PathVariable("organization-id") String organizationId,
                                            @PathVariable("project-id") String projectId,
                                            @RequestBody Set<String> audience) {
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        iamSecurityValidator.verifyProjectAdminAccess(orgId, projId);
        projectManagerService.setAudience(orgId, projId, audience);
        return ResponseEntity.ok().build();
    }

}
