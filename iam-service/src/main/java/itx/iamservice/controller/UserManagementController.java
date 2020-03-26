package itx.iamservice.controller;

import itx.iamservice.core.model.Role;
import itx.iamservice.core.model.UserId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping(path = "/services/management")
public class UserManagementController {

    @PostMapping(path = "/{organization-id}/projects/{project-id}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserId> createUser(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId) {
        //TODO
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteUser(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @PathVariable("user-id") String userId) {
        //TODO
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignRoleToUser(@PathVariable("organization-id") String organizationId,
                                                 @PathVariable("project-id") String projectId,
                                                 @PathVariable("user-id") String userId,
                                                 @PathVariable("role-id") String roleId) {
        //TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Role>> getRolesForUser(@PathVariable("organization-id") String organizationId,
                                                            @PathVariable("project-id") String projectId,
                                                            @PathVariable("user-id") String userId) {
        //TODO
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/users/{user-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("user-id") String userId,
                                                   @PathVariable("role-id") String roleId) {
        //TODO
        return ResponseEntity.ok().build();
    }

}
