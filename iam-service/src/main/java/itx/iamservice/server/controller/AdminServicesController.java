package itx.iamservice.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.admin.ClientManagementService;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.dto.CreateClientRequest;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;
import itx.iamservice.core.services.dto.CreateUserRequest;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static itx.iamservice.core.ModelCommons.createAdminPermissions;
import static itx.iamservice.core.ModelCommons.createAdminRoleId;

@RestController
@RequestMapping(path = "/services/admin")
@Tag(name = "Admin Services", description = "APIs for privileged admin users to perform aggregated actions.")
public class AdminServicesController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminServicesController.class);

    private final OrganizationManagerService organizationManagerService;
    private final ProjectManagerService projectManagerService;
    private final ClientManagementService clientManagementService;
    private final UserManagerService userManagerService;

    public AdminServicesController(OrganizationManagerService organizationManagerService,
                                   ProjectManagerService projectManagerService,
                                   ClientManagementService clientManagementService,
                                   UserManagerService userManagerService) {
        this.organizationManagerService = organizationManagerService;
        this.projectManagerService = projectManagerService;
        this.clientManagementService = clientManagementService;
        this.userManagerService = userManagerService;
    }

    @PostMapping("/organization/setup")
    public ResponseEntity<SetupOrganizationResponse> setUpOrganization(@RequestBody SetupOrganizationRequest request) throws PKIException {
        OrganizationId organizationId = OrganizationId.from(request.getOrganizationId());
        ProjectId projectId = ProjectId.from(request.getAdminProjectId());
        ClientId clientId = ClientId.from(request.getAdminClientId());
        UserId userId = UserId.from(request.getAdminUserId());
        RoleId adminRoleId = createAdminRoleId(organizationId, projectId);
        LOG.info("Organization setup {}/{}", organizationId.getId(), projectId.getId());
        //Data Check Actions
        if (clientManagementService.getClient(organizationId, projectId, clientId).isEmpty()) {
            CreateOrganizationRequest createOrganizationRequest = new CreateOrganizationRequest(organizationId, request.getOrganizationName());
            Optional<OrganizationId> organizationIdOptional = organizationManagerService.create(createOrganizationRequest);
            if (organizationIdOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }
        if (userManagerService.get(organizationId, projectId, userId).isEmpty()) {
            CreateProjectRequest createProjectRequest = new CreateProjectRequest(projectId, request.getAdminProjectName(), request.getProjectAudience());
            Optional<Project> projectOptional = projectManagerService.create(organizationId, createProjectRequest);
            if (projectOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }
        //Create Actions
        CreateClientRequest createClientRequest = new CreateClientRequest(clientId, "", 3600*1000L, 24*3600*1000L, request.getAdminClientSecret());
        Optional<ClientCredentials> clientCredentials = clientManagementService.createClient(organizationId, projectId, createClientRequest);
        if (clientCredentials.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CreateUserRequest createUserRequest =  new CreateUserRequest(userId, "", 3600*1000L, 24*3600*1000L);
        Optional<User> userOptional = userManagerService.create(organizationId, projectId, createUserRequest);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<RoleId> optionalAdminRoleId = projectManagerService.addRole(organizationId, projectId, new CreateRoleRequest(adminRoleId, "Admin Role"));
        if (optionalAdminRoleId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        clientManagementService.addRole(organizationId, projectId, clientId, adminRoleId);
        userManagerService.assignRole(organizationId, projectId, userId, adminRoleId);
        Set<String> adminPermissions = new HashSet<>();
        for (Permission permission: createAdminPermissions(organizationId, projectId)) {
            projectManagerService.addPermission(organizationId, projectId, permission);
            projectManagerService.addPermissionToRole(organizationId, projectId, adminRoleId, permission.getId());
            adminPermissions.add(permission.asStringValue());
        }
        return ResponseEntity.ok().body(new SetupOrganizationResponse(request, adminRoleId.getId(), adminPermissions));
    }

    @DeleteMapping("/organization/{organization-id}")
    public ResponseEntity<Void> deleteOrganizationRecursively(@PathVariable("organization-id") String organizationId) {
        boolean result = organizationManagerService.removeWithDependencies(OrganizationId.from(organizationId));
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/organization/{organization-id}/{project-id}")
    public ResponseEntity<Void> deleteProjectRecursively(@PathVariable("organization-id") String organizationId,
                                                         @PathVariable("project-id") String projectId) {
        boolean result = projectManagerService.removeWithDependencies(OrganizationId.from(organizationId), ProjectId.from(projectId));
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
