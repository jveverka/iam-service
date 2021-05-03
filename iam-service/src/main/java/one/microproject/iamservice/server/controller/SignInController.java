package one.microproject.iamservice.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import one.microproject.iamservice.core.services.dto.UserSignInConfirmationRequest;
import one.microproject.iamservice.core.services.dto.UserSignInRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/services/oauth2")
@Tag(name = "OAuth2", description = "APIs providing user sign-in self service.")
public class SignInController {

    @PostMapping(path = "/{organization-id}/{project-id}/signin")
    public ResponseEntity<Void> signIn(@PathVariable("organization-id") String organizationId,
                                       @PathVariable("project-id") String projectId,
                                       @RequestBody UserSignInRequest userSignInRequest) {
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/{organization-id}/{project-id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable("organization-id") String organizationId,
                                       @PathVariable("project-id") String projectId,
                                       @RequestBody UserSignInConfirmationRequest userSignInConfirmationRequest) {
        return ResponseEntity.ok().build();
    }

}
