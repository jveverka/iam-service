package one.microproject.iamservice.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.microproject.iamservice.core.dto.CreateOrganization;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.ClientProperties;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.Permission;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPCredentials;
import one.microproject.iamservice.core.services.admin.ClientManagementService;
import one.microproject.iamservice.core.services.admin.OrganizationManagerService;
import one.microproject.iamservice.core.services.admin.ProjectManagerService;
import one.microproject.iamservice.core.services.admin.UserManagerService;
import one.microproject.iamservice.core.services.dto.CreateClientRequest;
import one.microproject.iamservice.core.services.dto.CreateOrganizationRequest;
import one.microproject.iamservice.core.services.dto.CreateProjectRequest;
import one.microproject.iamservice.core.services.dto.CreateRoleRequest;
import one.microproject.iamservice.core.services.dto.CreateUserRequest;
import one.microproject.iamservice.core.services.dto.IdHolder;
import one.microproject.iamservice.core.services.dto.SetupOrganizationRequest;
import one.microproject.iamservice.core.services.dto.SetupOrganizationResponse;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static one.microproject.iamservice.core.ModelCommons.createProjectAdminPermissions;
import static one.microproject.iamservice.core.ModelCommons.createProjectAdminRoleId;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.normalize;

@RestController
@RequestMapping(path = "/services/admin")
@Tag(name = "Admin Services", description = "APIs for privileged admin users to perform basic Organization and Project actions.")
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
        LOG.info("Organization setup {}/{}", normalize(request.getOrganizationId()), normalize(request.getProjectId()));
        OrganizationId organizationId = OrganizationId.from(request.getOrganizationId());
        ProjectId projectId = ProjectId.from(request.getProjectId());
        ClientId clientId = ClientId.from(request.getAdminClientId());
        UserId userId = UserId.from(request.getAdminUserId());
        RoleId adminRoleId = createProjectAdminRoleId(organizationId, projectId);
        //Data Check Actions
        if (organizationManagerService.get(organizationId).isEmpty()) {
            CreateOrganizationRequest createOrganizationRequest = new CreateOrganizationRequest(organizationId, request.getOrganizationName());
            Optional<OrganizationId> organizationIdOptional = organizationManagerService.create(createOrganizationRequest);
            if (organizationIdOptional.isEmpty()) {
                LOG.warn("Error: create Organization id={} failed !", normalize(organizationId.getId()));
                return ResponseEntity.badRequest().build();
            } else {
                LOG.info("Organization id={} created !", normalize(organizationId.getId()));
            }
        }
        if (projectManagerService.get(organizationId, projectId).isEmpty()) {
            CreateProjectRequest createProjectRequest = new CreateProjectRequest(projectId, request.getProjectName(), request.getProjectAudience());
            Optional<Project> projectOptional = projectManagerService.create(organizationId, createProjectRequest);
            if (projectOptional.isEmpty()) {
                LOG.warn("Error: create Project id={} failed !", normalize(projectId.getId()));
                return ResponseEntity.badRequest().build();
            } else {
                LOG.info("Project id={} created !", normalize(projectId.getId()));
            }
        }
        //Create Actions
        ClientProperties properties = new ClientProperties(request.getRedirectURL(),
                true, true, false, new HashMap<>());
        CreateClientRequest createClientRequest = new CreateClientRequest(clientId, "", 3600*1000L, 24*3600*1000L,
                request.getAdminClientSecret(), properties);
        Optional<ClientCredentials> clientCredentials = clientManagementService.createClient(organizationId, projectId, createClientRequest);
        if (clientCredentials.isEmpty()) {
            LOG.warn("Error: create Client id={} failed !", normalize(clientId.getId()));
            return ResponseEntity.badRequest().build();
        } else {
            LOG.info("Client id={} created !", normalize(clientId.getId()));
        }

        CreateUserRequest createUserRequest =  new CreateUserRequest(userId, "", 3600*1000L, 24*3600*1000L,
                request.getAdminUserEmail(), request.getAdminUserProperties());
        Optional<User> userOptional = userManagerService.create(organizationId, projectId, createUserRequest);
        UPCredentials credentials = new UPCredentials(userId, request.getAdminUserPassword());
        userManagerService.setCredentials(organizationId, projectId, userId, credentials);
        if (userOptional.isEmpty()) {
            LOG.warn("Error: create User id={} failed !", normalize(userId.getId()));
            return ResponseEntity.badRequest().build();
        } else {
            LOG.info("User id={} created !", normalize(userId.getId()));
        }

        Optional<RoleId> optionalAdminRoleId = projectManagerService.addRole(organizationId, projectId, new CreateRoleRequest(adminRoleId, "Admin Role"));
        if (optionalAdminRoleId.isEmpty()) {
            LOG.warn("Error: create Role id={} failed !", adminRoleId.getId());
            return ResponseEntity.badRequest().build();
        } else {
            LOG.info("Role id={} created !", normalize(adminRoleId.getId()));
        }

        clientManagementService.addRole(organizationId, projectId, clientId, adminRoleId);
        userManagerService.assignRole(organizationId, projectId, userId, adminRoleId);
        Set<String> adminPermissions = new HashSet<>();
        for (Permission permission: createProjectAdminPermissions(organizationId, projectId)) {
            projectManagerService.addPermission(organizationId, projectId, permission);
            projectManagerService.addPermissionToRole(organizationId, projectId, adminRoleId, permission.getId());
            adminPermissions.add(permission.asStringValue());
        }
        LOG.info("setUpOrganization: OK");
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
