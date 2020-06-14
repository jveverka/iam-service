package itx.iamservice.server.controller;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.dto.CreateOrganizationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = "/services/management")
public class OrganizationManagementController {

    private final OrganizationManagerService organizationManagerService;

    public OrganizationManagementController(@Autowired OrganizationManagerService organizationManagerService) {
        this.organizationManagerService = organizationManagerService;
    }

    @PostMapping(path = "/organizations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrganizationId> createOrganization(@RequestBody CreateOrganizationRequest request) throws PKIException {
        Optional<OrganizationId> organizationIdOptional = organizationManagerService.create(request);
        if (organizationIdOptional.isPresent()) {
            return ResponseEntity.ok(organizationIdOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping(path = "/organizations/{organization-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteOrganization(@PathVariable("organization-id") String organizationId) {
        organizationManagerService.remove(OrganizationId.from(organizationId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
