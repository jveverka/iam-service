package itx.iamservice.server.controller;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import itx.iamservice.core.dto.CreateOrganization;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

import static itx.iamservice.core.ModelCommons.ADMIN_ORGANIZATION_SET;

@RestController
@RequestMapping(path = "/services/admin")
public class OrganizationManagementController {

    private final OrganizationManagerService organizationManagerService;
    private final IAMSecurityValidator iamSecurityValidator;

    public OrganizationManagementController(@Autowired OrganizationManagerService organizationManagerService,
                                            @Autowired IAMSecurityValidator iamSecurityValidator) {
        this.organizationManagerService = organizationManagerService;
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @PostMapping(path = "/organizations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrganizationId> createOrganization(@RequestBody CreateOrganization request,
                                                             @RequestHeader("Authorization") String authorization) throws PKIException {
        iamSecurityValidator.validate(ADMIN_ORGANIZATION_SET, Set.of(), authorization);
        Optional<OrganizationId> organizationIdOptional = organizationManagerService.create(CreateOrganizationRequest.from(request.getId(), request.getName()));
        if (organizationIdOptional.isPresent()) {
            return ResponseEntity.ok(organizationIdOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping(path = "/organizations/{organization-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteOrganization(@PathVariable("organization-id") String organizationId,
                                                   @RequestHeader("Authorization") String authorization) {
        iamSecurityValidator.validate(ADMIN_ORGANIZATION_SET, Set.of(), authorization);
        organizationManagerService.remove(OrganizationId.from(organizationId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
