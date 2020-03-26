package itx.iamservice.controller;

import itx.iamservice.core.model.Client;
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
public class ClientManagementController {

    @PostMapping(path = "/{organization-id}/projects/{project-id}/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Client> createClient(@PathVariable("organization-id") String organizationId,
                                               @PathVariable("project-id") String projectId) {
        //TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{organization-id}/projects/{project-id}/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Client>> getClients(@PathVariable("organization-id") String organizationId,
                                                         @PathVariable("project-id") String projectId) {
        //TODO
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/clients/{client-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteClient(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @PathVariable("client-id") String clientId) {
        //TODO
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{organization-id}/projects/{project-id}/clients/{client-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignRoleToClient(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("client-id") String clientId,
                                                   @PathVariable("role-id") String roleId) {
        //TODO
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{organization-id}/projects/{project-id}/clients/{client-id}/roles/{role-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeRoleFromClient(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @PathVariable("client-id") String clientId,
                                                   @PathVariable("role-id") String roleId) {
        //TODO
        return ResponseEntity.ok().build();
    }

}
