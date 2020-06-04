package itx.iamservice.server.controller;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.dto.CreatePermissionRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
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
                                                   @RequestBody CreateProjectRequest request) throws PKIException {
        OrganizationId id = OrganizationId.from(organizationId);
        Optional<ProjectId> projectId = projectManagerService.create(id, request);
        return ResponseEntity.of(projectId);
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProject(@PathVariable("organization-id") String organizationId,
                                              @PathVariable("project-id") String projectId) {
        projectManagerService.remove(OrganizationId.from(organizationId), ProjectId.from(projectId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Role Management Endpoints
     */

    @PostMapping(path = "/{organization-id}/projects/{project-id}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleId> createRole(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @RequestBody CreateRoleRequest request) {
        Optional<RoleId> roleId = projectManagerService.addRole(OrganizationId.from(organizationId), ProjectId.from(projectId), request);
        return ResponseEntity.of(roleId);
    }

    @GetMapping(path = "/{organization-id}/projects/{project-id}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Role>> getRoles(@PathVariable("organization-id") String organizationId,
                                                     @PathVariable("project-id") String projectId) {
        Collection<Role> roles = projectManagerService.getRoles(OrganizationId.from(organizationId), ProjectId.from(projectId));
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteRole(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @PathVariable("role-id") String roleId) {
        projectManagerService.removeRole(OrganizationId.from(organizationId), ProjectId.from(projectId), RoleId.from(roleId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Permission Management Endpoints
     */

    @PostMapping(path = "/{organization-id}/projects/{project-id}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionId> createPermission(@PathVariable("organization-id") String organizationId,
                                                         @PathVariable("project-id") String projectId,
                                                         @RequestBody CreatePermissionRequest request) {
        Permission permission = new Permission(request.getService(), request.getResource(), request.getAction());
        projectManagerService.addPermission(OrganizationId.from(organizationId), ProjectId.from(projectId), permission);
        return ResponseEntity.ok(permission.getId());
    }

    @GetMapping(path = "/{organization-id}/projects/{project-id}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Permission>> getPermissions(@PathVariable("organization-id") String organizationId,
                                                                 @PathVariable("project-id") String projectId) {
        Collection<Permission> permissions = projectManagerService.getPermissions(OrganizationId.from(organizationId), ProjectId.from(projectId));
        return ResponseEntity.ok(permissions);
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/permissions/{permission-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deletePermission(@PathVariable("organization-id") String organizationId,
                                                 @PathVariable("project-id") String projectId,
                                                 @PathVariable("permission-id") String permissionId) {
        projectManagerService.removePermission(OrganizationId.from(organizationId), ProjectId.from(projectId), PermissionId.from(permissionId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Role-Permission Assignment Management Endpoints
     */

    @PutMapping(path = "/{organization-id}/projects/{project-id}/roles-permissions/{role-id}/{permission-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPermissionToRole(@PathVariable("organization-id") String organizationId,
                                                    @PathVariable("project-id") String projectId,
                                                    @PathVariable("role-id") String roleId,
                                                    @PathVariable("permission-id") String permissionId) {
        projectManagerService.addPermissionToRole(OrganizationId.from(organizationId), ProjectId.from(projectId), RoleId.from(roleId), PermissionId.from(permissionId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/roles-permissions/{role-id}/{permission-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removePermissionFromRole(@PathVariable("organization-id") String organizationId,
                                                         @PathVariable("project-id") String projectId,
                                                         @PathVariable("role-id") String roleId,
                                                         @PathVariable("permission-id") String permissionId) {
        projectManagerService.removePermissionFromRole(OrganizationId.from(organizationId), ProjectId.from(projectId), RoleId.from(roleId), PermissionId.from(permissionId));
        return ResponseEntity.ok().build();
    }

}
