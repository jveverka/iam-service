package itx.iamservice.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/services/management")
@Tag(name = "Management", description = "APIs providing self-service user management.")
public class ManagementController {

    @PostMapping("/{organization-id}/{project-id}/clients")
    public ResponseEntity<Void> createClient(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId) {
        //TODO: add missing implementation
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{organization-id}/{project-id}/clients/{client-id}")
    public ResponseEntity<Void> deleteclient(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @PathVariable("cient-id") String clientId) {
        //TODO: add missing implementation
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{organization-id}/{project-id}/users")
    public ResponseEntity<Void> createUser(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId) {
        //TODO: add missing implementation
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{organization-id}/{project-id}/users/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @PathVariable("user-id") String userId) {
        //TODO: add missing implementation
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{organization-id}/{project-id}/users/{user-id}/change-password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("user-id") String userId) {
        //TODO: add missing implementation
        return ResponseEntity.ok().build();
    }

}
