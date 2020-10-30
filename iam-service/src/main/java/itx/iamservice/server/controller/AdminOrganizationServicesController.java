package itx.iamservice.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import itx.iamservice.core.dto.CreateOrganization;
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
import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;
import itx.iamservice.core.services.admin.ClientManagementService;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.dto.CreateClientRequest;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.core.services.dto.CreateRoleRequest;
import itx.iamservice.core.services.dto.CreateUserRequest;
import itx.iamservice.core.services.dto.IdHolder;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import static itx.iamservice.core.ModelCommons.createProjectAdminPermissions;
import static itx.iamservice.core.ModelCommons.createProjectAdminRoleId;

@RestController
@RequestMapping(path = "/services/admin")
@Tag(name = "Admin Services", description = "APIs for privileged admin users to perform basic Organization and Project actions.")
public class AdminOrganizationServicesController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminOrganizationServicesController.class);

    private final OrganizationManagerService organizationManagerService;
    private final ProjectManagerService projectManagerService;
    private final ClientManagementService clientManagementService;
    private final UserManagerService userManagerService;

    public AdminOrganizationServicesController(OrganizationManagerService organizationManagerService,
                                               ProjectManagerService projectManagerService,
                                               ClientManagementService clientManagementService,
                                               UserManagerService userManagerService) {
        this.organizationManagerService = organizationManagerService;
        this.projectManagerService = projectManagerService;
        this.clientManagementService = clientManagementService;
        this.userManagerService = userManagerService;
    }

    @Operation(summary = "Create new organization.")
    @PostMapping(path = "/organization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdHolder> createOrganization(@RequestBody CreateOrganization request) throws PKIException {
        Optional<OrganizationId> organizationIdOptional = organizationManagerService.create(CreateOrganizationRequest.from(request.getId(), request.getName()));
        if (organizationIdOptional.isPresent()) {
            return ResponseEntity.ok(IdHolder.from(organizationIdOptional.get().getId()));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Create new project within an organization, with default project admin user.")
    @PostMapping("/organization/setup")
    public ResponseEntity<SetupOrganizationResponse> setUpOrganization(@RequestBody SetupOrganizationRequest request) throws PKIException {
        OrganizationId organizationId = OrganizationId.from(request.getOrganizationId());
        ProjectId projectId = ProjectId.from(request.getAdminProjectId());
        ClientId clientId = ClientId.from(request.getAdminClientId());
        UserId userId = UserId.from(request.getAdminUserId());
        RoleId adminRoleId = createProjectAdminRoleId(organizationId, projectId);
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

        CreateUserRequest createUserRequest =  new CreateUserRequest(userId, "", 3600*1000L, 24*3600*1000L, request.getAdminUserEmail());
        Optional<User> userOptional = userManagerService.create(organizationId, projectId, createUserRequest);
        UPCredentials credentials = new UPCredentials(userId, request.getAdminUserPassword());
        userManagerService.setCredentials(organizationId, projectId, userId, credentials);
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
        for (Permission permission: createProjectAdminPermissions(organizationId, projectId)) {
            projectManagerService.addPermission(organizationId, projectId, permission);
            projectManagerService.addPermissionToRole(organizationId, projectId, adminRoleId, permission.getId());
            adminPermissions.add(permission.asStringValue());
        }
        return ResponseEntity.ok().body(new SetupOrganizationResponse(request, adminRoleId.getId(), adminPermissions));
    }

    @Operation(summary = "Delete organization by ID with all projects, users and clients.")
    @DeleteMapping("/organization/{organization-id}")
    public ResponseEntity<Void> deleteOrganizationRecursively(@PathVariable("organization-id") String organizationId) {
        boolean result = organizationManagerService.removeWithDependencies(OrganizationId.from(organizationId));
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete project in organization by ID with users and clients.")
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
