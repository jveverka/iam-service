package itx.iamservice.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.model.ClientCredentials;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.TokenType;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.AuthenticationService;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.ProviderConfigurationService;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.AuthorizationCodeGrantRequest;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.ConsentRequest;
import itx.iamservice.core.services.dto.GrantType;
import itx.iamservice.core.services.dto.IdTokenRequest;
import itx.iamservice.core.dto.IntrospectRequest;
import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.services.dto.ProviderConfigurationRequest;
import itx.iamservice.core.dto.ProviderConfigurationResponse;
import itx.iamservice.core.services.dto.RevokeTokenRequest;
import itx.iamservice.core.services.dto.Scope;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.core.services.dto.UserInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/services/authentication")
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private final ServletContext servletContext;
    private final AuthenticationService authenticationService;
    private final ProviderConfigurationService providerConfigurationService;
    private final ResourceServerService resourceServerService;
    private final ClientService clientService;
    private final ObjectMapper objectMapper;

    public AuthenticationController(@Autowired ServletContext servletContext,
                                    @Autowired AuthenticationService authenticationService,
                                    @Autowired ProviderConfigurationService providerConfigurationService,
                                    @Autowired ResourceServerService resourceServerService,
                                    @Autowired ClientService clientService,
                                    @Autowired ObjectMapper objectMapper) {
        this.servletContext = servletContext;
        this.authenticationService = authenticationService;
        this.providerConfigurationService = providerConfigurationService;
        this.resourceServerService = resourceServerService;
        this.clientService = clientService;
        this.objectMapper = objectMapper;
    }

    /**
    // TODO: workaround for insomnia client
    @GetMapping(path = "/{organization-id}/{project-id}/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> getTokens(@PathVariable("organization-id") String organizationId,
                                                   @PathVariable("project-id") String projectId,
                                                   @RequestParam("code") String code,
                                                   @RequestParam("state") String state,
                                                   @RequestParam(name = "nonce", required = false) String nonce,
                                                   HttpServletRequest request) {
        IdTokenRequest idTokenRequest = new IdTokenRequest(request.getRequestURL().toString(), nonce);
        LOG.info("getTokens: query={}", request.getRequestURL());
        LOG.info("getTokens: parameters=[{}]", getParameters(request.getParameterNames()));
        LOG.info("getTokens: org={} proj={} code={} state={} nonce={}", organizationId, projectId, code, state, nonce);
        Optional<TokenResponse> tokensOptional = authenticationService.authenticate(Code.from(code), idTokenRequest);
        return ResponseEntity.of(tokensOptional);
    }
    */

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
                                                   HttpServletRequest request) {
        LOG.info("postGetTokens: query={}", request.getRequestURL());
        LOG.info("postGetTokens: parameters=[{}]", getParameters(request.getParameterNames()));
        LOG.info("postGetTokens: nonce={} audience={}", nonce, audience);
        GrantType grantTypeEnum = GrantType.getGrantType(grantType);
        OrganizationId orgId = OrganizationId.from(organizationId);
        ProjectId projId = ProjectId.from(projectId);
        IdTokenRequest idTokenRequest = new IdTokenRequest(request.getRequestURL().toString(), nonce);
        if (GrantType.AUTHORIZATION_CODE.equals(grantTypeEnum)) {
            LOG.info("postGetTokens: grantType={} code={}", grantType, code);
            Optional<TokenResponse> tokensOptional = authenticationService.authenticate(Code.from(code), idTokenRequest);
            return ResponseEntity.of(tokensOptional);
        } else if (GrantType.PASSWORD.equals(grantTypeEnum)) {
            LOG.info("postGetTokens: grantType={} username={} scope={} clientId={}", grantType, username, scope, clientId);
            ClientCredentials clientCredentials = new ClientCredentials(ClientId.from(clientId), clientSecret);
            Scope scopes = ModelUtils.getScopes(scope);
            UPAuthenticationRequest upAuthenticationRequest = new UPAuthenticationRequest(UserId.from(username), password, scopes, clientCredentials);
            Optional<TokenResponse> tokensOptional = authenticationService.authenticate(orgId, projId, clientCredentials, upAuthenticationRequest, scopes, idTokenRequest);
            return ResponseEntity.of(tokensOptional);
        } else if (GrantType.CLIENT_CREDENTIALS.equals(grantTypeEnum)) {
            LOG.info("postGetTokens: grantType={} scope={} clientId={}", grantType, scope, clientId);
            ClientCredentials clientCredentials = new ClientCredentials(ClientId.from(clientId), clientSecret);
            Scope scopes = ModelUtils.getScopes(scope);
            Optional<TokenResponse> tokensOptional = authenticationService.authenticate(orgId, projId, clientCredentials, scopes, idTokenRequest);
            return ResponseEntity.of(tokensOptional);
        } else if (GrantType.REFRESH_TOKEN.equals(grantTypeEnum)) {
            LOG.info("postGetTokens: grantType={} scope={} clientId={} refreshToken={}", grantType, scope, clientId, refreshToken);
            JWToken jwToken = new JWToken(refreshToken);
            ClientCredentials clientCredentials = new ClientCredentials(ClientId.from(clientId), clientSecret);
            Scope scopes = ModelUtils.getScopes(scope);
            Optional<TokenResponse> tokensOptional = authenticationService.refreshTokens(orgId, projId, jwToken, clientCredentials, scopes, idTokenRequest);
            return ResponseEntity.of(tokensOptional);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
        result = result.replace("__context-path__", getContextPath());
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

    @GetMapping(path = "/{organization-id}/{project-id}/consent", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getConsent(@PathVariable("organization-id") String organizationId,
                                             @PathVariable("project-id") String projectId,
                                             @RequestParam("username") String username,
                                             @RequestParam("password") String password,
                                             @RequestParam("client_id") String clientId,
                                             @RequestParam("redirect_uri") String redirectUri,
                                             @RequestParam("state") String state,
                                             @RequestParam(name = "scope", required = false) String scope,
                                             HttpServletRequest request) {
        LOG.info("getConsent: {}?{}", request.getRequestURL(), request.getQueryString());
        LOG.info("getConsent: clientId={} redirectUri={} state={} scope={} username={}", clientId, redirectUri, state, scope, username);
        Scope scopes = ModelUtils.getScopes(scope);
        Optional<AuthorizationCode> authorizationCode = authenticationService.login(OrganizationId.from(organizationId), ProjectId.from(projectId),
                UserId.from(username), ClientId.from(clientId), password, scopes, state);
        if (authorizationCode.isPresent()) {
            // Authentication OK: proceed to consent screen
            String code = authorizationCode.get().getCode().getCodeValue();
            LOG.info("getConsent: code={}", code);
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("html/consent-form.html");
            String result = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
            result = result.replace("__context-path__", getContextPath());
            result = result.replace("__organization-id__", organizationId);
            result = result.replace("__project-id__", projectId);
            result = result.replace("__code__", code);
            result = result.replace("__state__", authorizationCode.get().getState());
            result = result.replace("\"__available_scopes__\"", renderScopesToJson(authorizationCode.get().getAvailableScopes()));
            result = result.replace("__random__", UUID.randomUUID().toString()); //to prevent form caching
            return ResponseEntity.ok(result);
        } else {
            // Authentication ERROR: show login error !
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("html/login-error.html");
            String result = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping(path = "/{organization-id}/{project-id}/login-with-scopes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getLoginWithScopes(@PathVariable("organization-id") String organizationId,
                                           @PathVariable("project-id") String projectId,
                                           @RequestParam("code") String code,
                                           @RequestParam("state") String state,
                                           @RequestParam("scope") String scope,
                                           HttpServletRequest request) throws Exception {
        LOG.info("getLoginWithScopes: {}?{}", request.getRequestURL(), request.getQueryString());
        LOG.info("getLoginWithScopes: code={} state={} scope={}", code, state, scope);
        URL url = new URL(request.getRequestURL().toString());
        String baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/services/authentication";
        String redirectUri = baseUrl + "/" + organizationId + "/" + projectId + "/token";
        URI redirectURI = new URI(redirectUri + "?code=" + code + "&state=" + state);
        LOG.info("Login OK: redirectURI={}",  redirectURI);
        authenticationService.setScope(new Code(code), ModelUtils.getScopes(scope));
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(redirectURI).build();
    }

    @GetMapping(path = "/{organization-id}/{project-id}/sign-up", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getSignUp(@PathVariable("organization-id") String organizationId,
                                            @PathVariable("project-id") String projectId,
                                            HttpServletRequest request) {
        LOG.info("getConsent: {}?{}", request.getRequestURL(), request.getQueryString());
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("html/signup-form.html");
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
        result = result.replace("__context-path__", getContextPath());
        result = result.replace("__organization-id__", organizationId);
        result = result.replace("__project-id__", projectId);
        result = result.replace("__random__", UUID.randomUUID().toString()); //to prevent form caching
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/{organization-id}/{project-id}/authorize-programmatic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorizationCode> authorizeProgrammatically(@PathVariable("organization-id") String organizationId,
                                                      @PathVariable("project-id") String projectId,
                                                      @RequestBody AuthorizationCodeGrantRequest request) {
        Scope scopes = new Scope(Set.copyOf(request.getScopes()));
        Optional<AuthorizationCode> authorizationCode = authenticationService.login(OrganizationId.from(organizationId), ProjectId.from(projectId),
                UserId.from(request.getUsername()), ClientId.from(request.getClientId()), request.getPassword(), scopes, request.getState());
        return ResponseEntity.of(authorizationCode);
    }

    @PostMapping(path = "/{organization-id}/{project-id}/consent-programmatic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> consentProgrammatically(@PathVariable("organization-id") String organizationId,
                                                        @PathVariable("project-id") String projectId,
                                                        @RequestBody ConsentRequest request) {
        Scope scopes = new Scope(Set.copyOf(request.getScopes()));
        if (authenticationService.setScope(request.getCode(), scopes)) {
            return ResponseEntity.ok().build();
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
        URL url = new URL(request.getRequestURL().toString());
        String baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/services/authentication";
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
        IntrospectRequest request = new IntrospectRequest(JWToken.from(token), getTokenType(tokenTypeHint));
        IntrospectResponse response = resourceServerService.introspect(OrganizationId.from(organizationId), ProjectId.from(projectId), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/{organization-id}/{project-id}/revoke", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> revoke(@PathVariable("organization-id") String organizationId,
                                       @PathVariable("project-id") String projectId,
                                       @RequestParam("token") String token,
                                       @RequestParam(name = "token_type_hint", required = false) String tokenTypeHint) {
        RevokeTokenRequest request = new RevokeTokenRequest(JWToken.from(token), getTokenType(tokenTypeHint));
        clientService.revoke(OrganizationId.from(organizationId), ProjectId.from(projectId), request);
        return ResponseEntity.ok().build();
    }

    //https://openid.net/specs/openid-connect-core-1_0.html#UserInfoRequest
    @GetMapping(path = "/{organization-id}/{project-id}/userinfo", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable("organization-id") String organizationId,
                                                        @PathVariable("project-id") String projectId,
                                                        HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            Optional<UserInfoResponse> response = clientService.getUserInfo(OrganizationId.from(organizationId), ProjectId.from(projectId), JWToken.from(token));
            return ResponseEntity.of(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private TokenType getTokenType(String tokenTypeHint) {
        if (tokenTypeHint == null) {
            return null;
        } else {
            return TokenType.getTokenType(tokenTypeHint);
        }
    }

    private String getParameters(Enumeration<String> parameters) {
        StringBuilder sb = new StringBuilder();
        while (parameters.hasMoreElements()) {
            sb.append(parameters.nextElement());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private String getContextPath() {
        String path = servletContext.getContextPath();
        if (path == null || path.isEmpty())  {
            return "";
        } else if ("/".equals(path)) {
            return "";
        } else if (!path.isEmpty() && !path.startsWith("/")) {
            return "/" + path;
        } else {
            return path;
        }
    }

    private String renderScopesToJson(Scope scopes) {
        try {
            return objectMapper.writeValueAsString(scopes.getValues());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

}
