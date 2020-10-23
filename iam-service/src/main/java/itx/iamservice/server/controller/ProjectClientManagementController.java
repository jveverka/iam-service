package itx.iamservice.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/services/management")
public class ProjectClientManagementController {

    @PostMapping("/{organization-id}/{project-id}/clients")
    public ResponseEntity<Void> createClient(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId) {
        //TODO: add missing implementation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{organization-id}/{project-id}/clients/{client-id}")
    public ResponseEntity<Void> deleteclient(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @PathVariable("cient-id") String clientId) {
        //TODO: add missing implementation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
