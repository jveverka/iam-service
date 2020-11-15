package one.microproject.iamservice.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.services.AuthenticationService;
import one.microproject.iamservice.core.services.ProviderConfigurationService;
import one.microproject.iamservice.core.services.ResourceServerService;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeGrantRequest;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.services.dto.ConsentRequest;
import one.microproject.iamservice.core.services.dto.GrantType;
import one.microproject.iamservice.core.services.dto.IdTokenRequest;
import one.microproject.iamservice.core.dto.IntrospectRequest;
import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.services.dto.ProviderConfigurationRequest;
import one.microproject.iamservice.core.dto.ProviderConfigurationResponse;
import one.microproject.iamservice.core.services.dto.RevokeTokenRequest;
import one.microproject.iamservice.core.services.dto.Scope;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.services.dto.UserInfoResponse;
import one.microproject.iamservice.server.controller.support.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static one.microproject.iamservice.server.controller.support.ControllerUtils.getBaseUrl;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.getClientCredentials;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.getContextPath;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.getIssuerUri;

@RestController
@RequestMapping(path = "/services/authentication")
@Tag(name = "Authentication", description = "APIs providing OAuth2 authentication flows.")
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private final ServletContext servletContext;
    private final AuthenticationService authenticationService;
    private final ProviderConfigurationService providerConfigurationService;
    private final ResourceServerService resourceServerService;

    public AuthenticationController(@Autowired ServletContext servletContext,
                                    @Autowired AuthenticationService authenticationService,
                                    @Autowired ProviderConfigurationService providerConfigurationService,
                                    @Autowired ResourceServerService resourceServerService) {
        this.servletContext = servletContext;
        this.authenticationService = authenticationService;
        this.providerConfigurationService = providerConfigurationService;
        this.resourceServerService = resourceServerService;
    }

    @PostMapping(path = "/{organization-id}/{project-id}/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> postGetTokens(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @RequestParam("grant_type") String grantType,
                                                   @RequestParam(name = "username", required = false) String username,
                                                   @RequestParam(name = "password", required = false) String password,
                                                   @RequestParam(name = "scope", required = false) String scope,
                                                   @RequestParam(name = "client_id", required = false) String clientId,
                                                   @RequestParam(name = "client_secret",  required = false) String clientSecret,
                                                   @RequestParam(name = "refresh_token", required = false) String refreshToken,
                                                   @RequestParam(name = "code", required = false) String code,
                                                   @RequestParam(name = "nonce", required = false) String nonce,
                                                   @RequestParam(name = "audience", required = false) String audience,
                                                   HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        LOG.info("postGetTokens: query={}", request.getRequestURL());
        LOG.info("postGetTokens: parameters=[{}]", ControllerUtils.getParameters(request.getParameterNames()));
        LOG.info("postGetTokens: nonce={} audience={}", nonce, audience);
        URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        LOG.info("postGetTokens: IssuerUri={}", issuerUri);
        GrantType grantTypeEnum = GrantType.getGrantType(grantType);
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        IdTokenRequest idTokenRequest = new IdTokenRequest(request.getRequestURL().toString(), nonce);
        if (GrantType.AUTHORIZATION_CODE.equals(grantTypeEnum)) {
            LOG.info("postGetTokens: grantType={} code={}", grantType, code);
            Optional<TokenResponse> tokensOptional = authenticationService.authenticate(Code.from(code), idTokenRequest);
            return ResponseEntity.of(tokensOptional);
        } else if (GrantType.PASSWORD.equals(grantTypeEnum)) {
            Optional<ClientCredentials> ccOptional = getClientCredentials(request, clientId, clientSecret);
            if (ccOptional.isPresent()) {
                ClientCredentials clientCredentials = ccOptional.get();
                LOG.info("postGetTokens: grantType={} username={} scope={} clientId={}", grantType, username, scope, clientCredentials.getId().getId());
                Scope scopes = ModelUtils.getScopes(scope);
                UPAuthenticationRequest upAuthenticationRequest = new UPAuthenticationRequest(UserId.from(username), password, scopes, clientCredentials);
                Optional<TokenResponse> tokensOptional = authenticationService.authenticate(issuerUri, orgId, projId, clientCredentials, scopes, upAuthenticationRequest, idTokenRequest);
                return ResponseEntity.of(tokensOptional);
            } else {
                LOG.warn("Can't get client credentials !");
            }
        } else if (GrantType.CLIENT_CREDENTIALS.equals(grantTypeEnum)) {
            Optional<ClientCredentials> ccOptional = getClientCredentials(request, clientId, clientSecret);
            if (ccOptional.isPresent()) {
                ClientCredentials clientCredentials = ccOptional.get();
                LOG.info("postGetTokens: grantType={} scope={} clientId={}", grantType, scope, clientCredentials.getId().getId());
                Scope scopes = ModelUtils.getScopes(scope);
                Optional<TokenResponse> tokensOptional = authenticationService.authenticate(issuerUri, orgId, projId, clientCredentials, scopes, idTokenRequest);
                return ResponseEntity.of(tokensOptional);
            } else {
                LOG.warn("Can't get client credentials !");
            }
        } else if (GrantType.REFRESH_TOKEN.equals(grantTypeEnum)) {
            Optional<ClientCredentials> ccOptional = getClientCredentials(request, clientId, clientSecret);
            if (ccOptional.isPresent()) {
                ClientCredentials clientCredentials = ccOptional.get();
                LOG.info("postGetTokens: grantType={} scope={} clientId={} refreshToken={}", grantType, scope, clientCredentials.getId().getId(), refreshToken);
                JWToken jwToken = new JWToken(refreshToken);
                Scope scopes = ModelUtils.getScopes(scope);
                Optional<TokenResponse> tokensOptional = authenticationService.refreshTokens(orgId, projId, jwToken, clientCredentials, scopes, idTokenRequest);
                return ResponseEntity.of(tokensOptional);
            } else {
                LOG.warn("Can't get client credentials !");
            }
        } else {
            LOG.warn("Unsupported grant_type={} !", grantType);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping(path = "/{organization-id}/{project-id}/authorize", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getAuthorize(@PathVariable("organization-id") String organizationId,
                                               @PathVariable("project-id") String projectId,
                                               @RequestParam("response_type") String responseType,
                                               @RequestParam("client_id") String clientId,
                                               @RequestParam("redirect_uri") String redirectUri,
                                               @RequestParam("state") String state,
                                               @RequestParam(name = "scope", required = false) String scope,
                                               HttpServletRequest request) {
        LOG.info("getAuthorize: {}?{}", request.getRequestURL(), request.getQueryString());
        LOG.info("getAuthorize: {}/{} responseType={} clientId={} redirectUri={} state={} scope={}", organizationId, projectId, responseType, clientId, redirectUri, state, scope);
        if (scope==null) {
            scope = "";
        }
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("html/login-form.html");
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
        result = result.replace("__context-path__", getContextPath(servletContext));
        result = result.replace("__organization-id__", organizationId);
        result = result.replace("__project-id__", projectId);
        result = result.replace("__response-type__", responseType);
        result = result.replace("__client_id__", clientId);
        result = result.replace("__redirect_uri__", redirectUri);
        result = result.replace("__state__", state);
        result = result.replace("__scope__", scope);
        result = result.replace("__random__", UUID.randomUUID().toString()); //to prevent form caching
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/{organization-id}/{project-id}/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorizationCode> authorizeProgrammatically(@PathVariable("organization-id") String organizationId,
                                                                       @PathVariable("project-id") String projectId,
                                                                       @RequestBody AuthorizationCodeGrantRequest authorizationCodeGrantRequest,
                                                                       HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        LOG.info("authorizeProgrammatically: {}/{}", organizationId, projectId);
        Scope scopes = new Scope(Set.copyOf(authorizationCodeGrantRequest.getScopes()));
        URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        Optional<AuthorizationCode> authorizationCode = authenticationService.login(issuerUri, OrganizationId.from(organizationId), ProjectId.from(projectId),
                UserId.from(authorizationCodeGrantRequest.getUsername()), ClientId.from(authorizationCodeGrantRequest.getClientId()),
                authorizationCodeGrantRequest.getPassword(), scopes, authorizationCodeGrantRequest.getState(),
                authorizationCodeGrantRequest.getRedirectUri());
        return ResponseEntity.of(authorizationCode);
        //TODO: redirect to 'redirect URL ?' (redirect to URL associated with clientID which initiated flow ?)
    }

    @PostMapping(path = "/{organization-id}/{project-id}/consent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> consentProgrammatically(@PathVariable("organization-id") String organizationId,
                                                        @PathVariable("project-id") String projectId,
                                                        @RequestBody ConsentRequest request) {
        LOG.info("consentProgrammatically: {}/{}", organizationId, projectId);
        Scope scopes = new Scope(Set.copyOf(request.getScopes()));
        if (authenticationService.setScope(request.getCode(), scopes)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping(path = "/{organization-id}/{project-id}/redirect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> redirect(@PathVariable("organization-id") String organizationId,
                                                      @PathVariable("project-id") String projectId,
                                                      @RequestParam("code") String code,
                                                      @RequestParam("state") String state,
                                                      HttpServletRequest request) throws URISyntaxException, MalformedURLException {
        LOG.info("redirect: {}/{} code={} state={}", organizationId, projectId, code, state);
        RestTemplate restTemplate = new RestTemplate();
        URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        String tokenUrl = issuerUri.toString() + "/token" + "?grant_type=authorization_code&code=" + code + "&state=" + state;
        ResponseEntity<TokenResponse> tokenResponseResponseEntity = restTemplate.postForEntity(tokenUrl, null, TokenResponse.class);
        if (HttpStatus.OK.equals(tokenResponseResponseEntity.getStatusCode())) {
            return ResponseEntity.ok(tokenResponseResponseEntity.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    //https://openid.net/specs/openid-connect-discovery-1_0.html
    @GetMapping(path = "/{organization-id}/{project-id}/.well-known/openid-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProviderConfigurationResponse> getConfiguration(@PathVariable("organization-id") String organizationId,
                                                                          @PathVariable("project-id") String projectId,
                                                                          HttpServletRequest request) throws MalformedURLException {
        LOG.info("getConfiguration: {}", request.getRequestURL());
        String baseUrl = getBaseUrl(servletContext, request);
        ProviderConfigurationRequest providerConfigurationRequest = new ProviderConfigurationRequest(baseUrl, OrganizationId.from(organizationId), ProjectId.from(projectId));
        ProviderConfigurationResponse configuration = providerConfigurationService.getConfiguration(providerConfigurationRequest);
        return ResponseEntity.ok(configuration);
    }

    //https://tools.ietf.org/html/rfc7517
    @GetMapping(path = "/{organization-id}/{project-id}/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JWKResponse> getCerts(@PathVariable("organization-id") String organizationId,
                                                @PathVariable("project-id") String projectId) {
        LOG.info("getCerts: organizationId={} projectId={}", organizationId, projectId);
        JWKResponse jwkData = providerConfigurationService.getJWKData(OrganizationId.from(organizationId), ProjectId.from(projectId));
        return ResponseEntity.ok(jwkData);
    }

    //https://tools.ietf.org/html/rfc7662
    @PostMapping(path = "/{organization-id}/{project-id}/introspect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IntrospectResponse> introspectToken(@PathVariable("organization-id") String organizationId,
                                                              @PathVariable("project-id") String projectId,
                                                              @RequestParam("token") String token,
                                                              @RequestParam(name = "token_type_hint", required = false) String tokenTypeHint) {
        LOG.info("introspectToken: token={} token_type_hint={}", token, tokenTypeHint);
        IntrospectRequest request = new IntrospectRequest(JWToken.from(token), ControllerUtils.getTokenType(tokenTypeHint));
        IntrospectResponse response = resourceServerService.introspect(OrganizationId.from(organizationId), ProjectId.from(projectId), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/{organization-id}/{project-id}/revoke", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> revoke(@PathVariable("organization-id") String organizationId,
                                       @PathVariable("project-id") String projectId,
                                       @RequestParam("token") String token,
                                       @RequestParam(name = "token_type_hint", required = false) String tokenTypeHint) {
        RevokeTokenRequest request = new RevokeTokenRequest(JWToken.from(token), ControllerUtils.getTokenType(tokenTypeHint));
        authenticationService.revoke(OrganizationId.from(organizationId), ProjectId.from(projectId), request);
        return ResponseEntity.ok().build();
    }

    //https://openid.net/specs/openid-connect-core-1_0.html#UserInfoRequest
    @GetMapping(path = "/{organization-id}/{project-id}/userinfo", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable("organization-id") String organizationId,
                                                        @PathVariable("project-id") String projectId,
                                                        HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            Optional<UserInfoResponse> response = authenticationService.getUserInfo(OrganizationId.from(organizationId), ProjectId.from(projectId), JWToken.from(token));
            return ResponseEntity.of(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
