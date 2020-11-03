package one.microproject.iamservice.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import one.microproject.iamservice.core.dto.CreateClient;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.admin.ClientManagementService;
import one.microproject.iamservice.core.services.dto.CreateClientRequest;
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
@Tag(name = "Project Client Management", description = "APIs for managing Project's Clients.")
public class ProjectClientManagementController {

    private final ClientManagementService clientManagementService;
    private final IAMSecurityValidator iamSecurityValidator;

    public ProjectClientManagementController(ClientManagementService clientManagementService,
                                             IAMSecurityValidator iamSecurityValidator) {
        this.clientManagementService = clientManagementService;
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @PostMapping("/{organization-id}/{project-id}/clients")
    public ResponseEntity<Void> createClient(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @RequestBody CreateClient createClient) {
        iamSecurityValidator.verifyProjectAdminAccess(OrganizationId.from(organizationId), ProjectId.from(projectId));
        CreateClientRequest request = new CreateClientRequest(ClientId.from(createClient.getId()),
                createClient.getName(), createClient.getDefaultAccessTokenDuration(),
                createClient.getDefaultRefreshTokenDuration(), createClient.getSecret(),
                createClient.getProperties());
        Optional<ClientCredentials> client = clientManagementService.createClient(OrganizationId.from(organizationId), ProjectId.from(projectId), request);
        if (client.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{organization-id}/{project-id}/clients/{client-id}/roles/{role-id}")
    public ResponseEntity<Void> addRoleToClient(@PathVariable("organization-id") String organizationId,
                                                @PathVariable("project-id") String projectId,
                                                @PathVariable("client-id") String clientId,
                                                @PathVariable("role-id") String roleId) {
        iamSecurityValidator.verifyProjectAdminAccess(OrganizationId.from(organizationId), ProjectId.from(projectId));
        if (clientManagementService.addRole(OrganizationId.from(organizationId), ProjectId.from(projectId), ClientId.from(clientId), RoleId.from(roleId))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{organization-id}/{project-id}/clients/{client-id}/roles/{role-id}")
    public ResponseEntity<Void> removeRoleFromClient(@PathVariable("organization-id") String organizationId,
                                                     @PathVariable("project-id") String projectId,
                                                     @PathVariable("client-id") String clientId,
                                                     @PathVariable("role-id") String roleId) {
        iamSecurityValidator.verifyProjectAdminAccess(OrganizationId.from(organizationId), ProjectId.from(projectId));
        if (clientManagementService.removeRole(OrganizationId.from(organizationId), ProjectId.from(projectId), ClientId.from(clientId), RoleId.from(roleId))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{organization-id}/{project-id}/clients/{client-id}")
    public ResponseEntity<Void> deleteClient(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @PathVariable("client-id") String clientId) {
        iamSecurityValidator.verifyProjectAdminAccess(OrganizationId.from(organizationId), ProjectId.from(projectId));
        if (clientManagementService.removeClient(OrganizationId.from(organizationId), ProjectId.from(projectId), ClientId.from(clientId))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
