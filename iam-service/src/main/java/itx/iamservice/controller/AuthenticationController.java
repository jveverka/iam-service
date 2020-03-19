package itx.iamservice.controller;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.services.AuthenticationService;
import itx.iamservice.services.dto.TokenRequest;
import itx.iamservice.services.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = "/services/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(@Autowired AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/{organization-id}/{project-id}/token", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<TokenResponse> getTokens(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @RequestParam("grant_type") String grantType,
                                                   @RequestParam(name = "username", required = false) String username,
                                                   @RequestParam(name = "password", required = false) String password,
                                                   @RequestParam(name = "scope", required = false) String scope,
                                                   @RequestParam("client_id") String clientId,
                                                   @RequestParam("client_secret") String clientSecret) {
        if (scope == null) {
            scope = "";
        }
        TokenRequest tokenRequest = new TokenRequest(grantType, username, password, scope, clientId, clientSecret);
        Optional<TokenResponse> response = authenticationService.getTokens(OrganizationId.from(organizationId), ProjectId.from(projectId), tokenRequest);
        return ResponseEntity.of(response);
    }

}
