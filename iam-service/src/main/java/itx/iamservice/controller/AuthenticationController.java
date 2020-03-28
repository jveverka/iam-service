package itx.iamservice.controller;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.services.AuthenticationService;
import itx.iamservice.core.services.dto.GrantType;
import itx.iamservice.services.dto.TokenRequest;
import itx.iamservice.core.services.dto.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/services/authentication")
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    public AuthenticationController(@Autowired AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/{organization-id}/{project-id}/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> getTokens(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @RequestParam("grant_type") String grantType,
                                                   @RequestParam(name = "username", required = false) String username,
                                                   @RequestParam(name = "password", required = false) String password,
                                                   @RequestParam(name = "scope", required = false) String scope,
                                                   @RequestParam(name = "client_id", required = false) String clientId,
                                                   @RequestParam(name = "client_secret",  required = false) String clientSecret,
                                                   @RequestParam(name = "refresh_token", required = false) String refreshToken,
                                                   @RequestParam(name = "code", required = false) String code) {
        GrantType grantTypeEnum = GrantType.getGrantType(grantType);
        if (GrantType.AUTHORIZATION_CODE.equals(grantTypeEnum)) {
            LOG.info("processRedirect: code={} grantType={}", code, grantType);
            Optional<TokenResponse> tokensOptional = authenticationService.authenticate(Code.from(code));
            return ResponseEntity.of(tokensOptional);
        } else {
            TokenRequest tokenRequest = new TokenRequest(grantType, username, password, scope, clientId, clientSecret, refreshToken);
            Optional<TokenResponse> tokensOptional = authenticationService.getTokens(OrganizationId.from(organizationId), ProjectId.from(projectId), tokenRequest);
            return ResponseEntity.of(tokensOptional);
        }
    }

    @GetMapping(path = "/{organization-id}/{project-id}/auth", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getAuth(@PathVariable("organization-id") String organizationId,
                                          @PathVariable("project-id") String projectId,
                                          @RequestParam("response_type") String responseType,
                                          @RequestParam("client_id") String clientId,
                                          @RequestParam("redirect_uri") String redirectUri,
                                          @RequestParam("state") String state,
                                          @RequestParam(name = "scope", required = false) String scope) {
        LOG.info("getAuth: {}/{} responseType={} clientId={} redirectUri={} state={} scope={}", organizationId, projectId, responseType, clientId, redirectUri, state, scope);
        if (scope==null) scope = "";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("html/login-form.html");
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
        result = result.replaceAll("__organization-id__", organizationId);
        result = result.replaceAll("__project-id__", projectId);
        result = result.replaceAll("__response-type__", responseType);
        result = result.replaceAll("__client_id__", clientId);
        result = result.replaceAll("__redirect_uri__", redirectUri);
        result = result.replaceAll("__state__", state);
        result = result.replaceAll("__scope__", scope);
        result = result.replaceAll("__random__", UUID.randomUUID().toString()); //to prevent form caching
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLogin(@RequestParam("organization-id") String organizationId,
                                           @RequestParam("project-id") String projectId,
                                           @RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                           @RequestParam("client_id") String clientId,
                                           @RequestParam("redirect_uri") String redirectUri,
                                           @RequestParam("state") String state,
                                           @RequestParam(name = "scope", required = false) String scope) throws URISyntaxException {
        LOG.info("getLogin: clientId={} redirectUri={} state={} scope={} username={}", clientId, redirectUri, state, scope, username);
        Optional<AuthorizationCode> authorizationCode = authenticationService.login(OrganizationId.from(organizationId), ProjectId.from(projectId),
                UserId.from(username), ClientId.from(clientId), password, scope, state);
        if (authorizationCode.isPresent())  {
            URI redirectURI = new URI(redirectUri + "?code=" + authorizationCode.get().getCode() + "&state=" + state);
            LOG.info("Login OK: redirectURI={}",  redirectURI);
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(redirectURI).build();
        } else {
            String errorDescription = "login-failed";
            URI redirectURI = new URI(redirectUri + "?error=invalid_request&error_description=" + errorDescription + "&state=" + state);
            LOG.info("Login Failed: redirectURI={}",  redirectURI);
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectURI).build();
        }
    }

}
