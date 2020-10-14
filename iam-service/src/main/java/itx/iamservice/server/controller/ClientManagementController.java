package itx.iamservice.server.controller;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.services.admin.ClientManagementService;
import itx.iamservice.core.services.dto.CreateClientRequest;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static itx.iamservice.core.ModelCommons.ADMIN_PROJECT_SET;
import static itx.iamservice.core.ModelCommons.getProjectAdminPermissionSet;
import static itx.iamservice.server.controller.ControllerUtils.getIssuerUri;

@RestController
@RequestMapping(path = "/services/admin")
public class ClientManagementController {

    private final ServletContext servletContext;
    private final ClientManagementService clientManagementService;
    private final IAMSecurityValidator iamSecurityValidator;

    public ClientManagementController(@Autowired ServletContext servletContext,
                                      @Autowired ClientManagementService clientManagementService,
                                      @Autowired IAMSecurityValidator iamSecurityValidator) {
        this.servletContext = servletContext;
        this.clientManagementService = clientManagementService;
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @PostMapping(path = "/{organization-id}/projects/{project-id}/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientCredentials> createClient(@PathVariable("organization-id") String organizationId,
                                                          @PathVariable("project-id") String projectId,
                                                          @RequestBody CreateClientRequest createClientRequest,
                                                          @RequestHeader("Authorization") String authorization,
                                                          HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        //URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        //iamSecurityValidator.validate(issuerUri, OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        iamSecurityValidator.validate(ADMIN_PROJECT_SET, applicationPermissions, authorization);
        Optional<ClientCredentials> client = clientManagementService.createClient(OrganizationId.from(organizationId), ProjectId.from(projectId), createClientRequest);
        if (client.isPresent()) {
            return ResponseEntity.ok(client.get());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(path = "/{organization-id}/projects/{project-id}/clients/{client-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Client> getClient(@PathVariable("organization-id") String organizationId,
                                            @PathVariable("project-id") String projectId,
                                            @PathVariable("client-id") String clientId,
                                            @RequestHeader("Authorization") String authorization,
                                            HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        //URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        //iamSecurityValidator.validate(issuerUri, OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        iamSecurityValidator.validate(ADMIN_PROJECT_SET, applicationPermissions, authorization);
        Optional<Client> client = clientManagementService.getClient(OrganizationId.from(organizationId), ProjectId.from(projectId), ClientId.from(clientId));
        return ResponseEntity.of(client);
    }

    @GetMapping(path = "/{organization-id}/projects/{project-id}/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Client>> getClients(@PathVariable("organization-id") String organizationId,
                                                         @PathVariable("project-id") String projectId,
                                                         @RequestHeader("Authorization") String authorization,
                                                         HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        //URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        //iamSecurityValidator.validate(issuerUri, OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        iamSecurityValidator.validate(ADMIN_PROJECT_SET, applicationPermissions, authorization);
        Collection<Client> clients = clientManagementService.getClients(OrganizationId.from(organizationId), ProjectId.from(projectId));
        return ResponseEntity.ok(clients);
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/clients/{client-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteClient(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @PathVariable("client-id") String clientId,
                                             @RequestHeader("Authorization") String authorization,
                                             HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        //URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        //iamSecurityValidator.validate(issuerUri, OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        iamSecurityValidator.validate(ADMIN_PROJECT_SET, applicationPermissions, authorization);
        boolean result = clientManagementService.removeClient(OrganizationId.from(organizationId), ProjectId.from(projectId), ClientId.from(clientId));
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(path = "/{organization-id}/projects/{project-id}/clients/{client-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignRoleToClient(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("client-id") String clientId,
                                                   @PathVariable("role-id") String roleId,
                                                   @RequestHeader("Authorization") String authorization,
                                                   HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        //URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        //iamSecurityValidator.validate(issuerUri, OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        iamSecurityValidator.validate(ADMIN_PROJECT_SET, applicationPermissions, authorization);
        boolean result = clientManagementService.addRole(OrganizationId.from(organizationId), ProjectId.from(projectId), ClientId.from(clientId), RoleId.from(roleId));
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/clients/{client-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeRoleFromClient(@PathVariable("organization-id") String organizationId,
                                                     @PathVariable("project-id") String projectId,
                                                     @PathVariable("client-id") String clientId,
                                                     @PathVariable("role-id") String roleId,
                                                     @RequestHeader("Authorization") String authorization,
                                                     HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        Set<Permission> applicationPermissions = getProjectAdminPermissionSet(OrganizationId.from(organizationId), ProjectId.from(projectId));
        //URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        //iamSecurityValidator.validate(issuerUri, OrganizationId.from(organizationId), ProjectId.from(projectId), ADMIN_PROJECT_SET, applicationPermissions, authorization);
        iamSecurityValidator.validate(ADMIN_PROJECT_SET, applicationPermissions, authorization);
        boolean result = clientManagementService.removeRole(OrganizationId.from(organizationId), ProjectId.from(projectId), ClientId.from(clientId), RoleId.from(roleId));
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
