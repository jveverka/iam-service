package itx.iamservice.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.admin.ClientManagementService;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.services.dto.CreateProjectRequest;
import itx.iamservice.server.dto.SetupOrganizationRequest;
import itx.iamservice.server.dto.SetupOrganizationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = "/services/admin")
@Tag(name = "Admin Services", description = "APIs for privileged admin users to perform aggregated actions.")
public class AdminServicesController {

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

    @PostMapping("/organization")
    public ResponseEntity<SetupOrganizationResponse> setUpOrganization(@RequestBody SetupOrganizationRequest request) throws PKIException {
        OrganizationId organizationId = OrganizationId.from(request.getOrganizationId());
        CreateOrganizationRequest createOrganizationRequest = new CreateOrganizationRequest(organizationId, request.getOrganizationName());
        Optional<OrganizationId> organizationIdOptional = organizationManagerService.create(createOrganizationRequest);
        if (organizationIdOptional.isPresent()) {
            CreateProjectRequest createProjectRequest = new CreateProjectRequest();
            projectManagerService.create(organizationId, createProjectRequest);
            return ResponseEntity.ok().body(new SetupOrganizationResponse());
        }
        return ResponseEntity.badRequest().build();
    }

}
