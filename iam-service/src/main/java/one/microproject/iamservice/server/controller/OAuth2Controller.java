package one.microproject.iamservice.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.microproject.iamservice.core.dto.ErrorType;
import one.microproject.iamservice.core.dto.TokenResponseError;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPAuthenticationRequest;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.services.AuthenticationService;
import one.microproject.iamservice.core.services.ProviderConfigurationService;
import one.microproject.iamservice.core.services.ResourceServerService;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
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
import one.microproject.iamservice.server.services.BaseUrlMapper;
import one.microproject.iamservice.server.controller.support.ControllerUtils;
import one.microproject.iamservice.server.controller.support.OAuth2TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
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

import static one.microproject.iamservice.client.JWTUtils.AUTHORIZATION;
import static one.microproject.iamservice.client.JWTUtils.BEARER_PREFIX;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.getBaseUrl;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.getClientCredentials;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.getCodeVerifier;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.getContextPath;
import static one.microproject.iamservice.server.controller.support.ControllerUtils.getIssuerUri;

@RestController
@RequestMapping(path = "/services/oauth2")
@Tag(name = "OAuth2", description = "APIs providing OAuth2 authentication flows.")
public class OAuth2Controller {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Controller.class);

    private final ServletContext servletContext;
    private final AuthenticationService authenticationService;
    private final ProviderConfigurationService providerConfigurationService;
    private final ResourceServerService resourceServerService;
    private final BaseUrlMapper baseUrlMapper;

    public OAuth2Controller(@Autowired ServletContext servletContext,
                            @Autowired AuthenticationService authenticationService,
                            @Autowired ProviderConfigurationService providerConfigurationService,
                            @Autowired ResourceServerService resourceServerService,
                            @Autowired BaseUrlMapper baseUrlMapper) {
        this.servletContext = servletContext;
        this.authenticationService = authenticationService;
        this.providerConfigurationService = providerConfigurationService;
        this.resourceServerService = resourceServerService;
        this.baseUrlMapper = baseUrlMapper;
    }

    @Operation(description =
            "This endpoint represents the end of all authorizations flows when if successful, tokens are issued.\n" +
            "Get Access Tokens for authorizations flows: \n" +
            "- [grant_type=refresh_token](https://tools.ietf.org/html/rfc6749#section-2.3.1) \n" +
            "- [grant_type=authorization_code](https://tools.ietf.org/html/rfc6749#section-4.1.3) \n" +
            "- [grant_type=password](https://tools.ietf.org/html/rfc6749#section-4.3.2) \n" +
            "- [grant_type=client_credentials](https://tools.ietf.org/html/rfc6749#section-4.4.2) \n" +
            "- [grant_type=refresh_token](https://tools.ietf.org/html/rfc6749#section-6) \n",
            parameters = {
                    @Parameter(name = "organization-id", description = "Unique organization identifier.", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "project-id", description = "Unique project identifier.", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "grant_type", description = "Grant type.", in = ParameterIn.QUERY, required = true),
                    @Parameter(name = "username", description = "User name.", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "password", description = "Password.", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "scope", description = "Scope.", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "client_id", description = "Client Id.", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "client_secret", description = "Client secret.", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "refresh_token", description = "Refresh token.", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "code", description = "Code.", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "nonce", description = "Nonce.", in = ParameterIn.QUERY, required = false),
                    @Parameter(name = "audience", description = "Audience.", in = ParameterIn.QUERY, required = false),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "MultiValueMap",
                    content = { @Content( mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE, schema = @Schema(implementation = MultiValueMap.class) ) },
                    required = false),
            responses = {
                    @ApiResponse(description = "Access Tokens", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TokenResponse.class))
                    )
            })
    @PostMapping(path = "/{organization-id}/{project-id}/token",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    public ResponseEntity<TokenResponse> postGetTokens(@PathVariable("organization-id") String organizationId,
                                                       @PathVariable("project-id") String projectId,
                                                       @RequestParam(name = "grant_type", required = true) String grantType,
                                                       @RequestParam(name = "username", required = false) String username,
                                                       @RequestParam(name = "password", required = false) String password,
                                                       @RequestParam(name = "scope", required = false) String scope,
                                                       @RequestParam(name = "client_id", required = false) String clientId,
                                                       @RequestParam(name = "client_secret",  required = false) String clientSecret,
                                                       @RequestParam(name = "refresh_token", required = false) String refreshToken,
                                                       @RequestParam(name = "code", required = false) String code,
                                                       @RequestParam(name = "nonce", required = false) String nonce,
                                                       @RequestParam(name = "audience", required = false) String audience,
                                                       @RequestBody MultiValueMap bodyValueMap,
                                                       HttpServletRequest request) throws OAuth2TokenException {
        try {
            LOG.info("postGetTokens: query={}", request.getRequestURL());
            LOG.info("postGetTokens: parameters=[{}]", ControllerUtils.getParameters(request.getParameterNames()));
            LOG.info("postGetTokens: nonce={} audience={}", nonce, audience);
            URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
            String codeVerifier = getCodeVerifier(bodyValueMap);
            LOG.info("postGetTokens: IssuerUri={} tokenVerifier={}", issuerUri, codeVerifier);
            GrantType grantTypeEnum = GrantType.getGrantType(grantType);
            OrganizationId orgId = OrganizationId.from(organizationId);
            ProjectId projId = ProjectId.from(projectId);
            IdTokenRequest idTokenRequest = new IdTokenRequest(request.getRequestURL().toString(), nonce, codeVerifier);
            if (GrantType.AUTHORIZATION_CODE.equals(grantTypeEnum)) {
                LOG.info("postGetTokens: grantType={} code={}", grantType, code);
                Optional<TokenResponse> tokensOptional = authenticationService.authenticate(Code.from(code), idTokenRequest);
                if (tokensOptional.isPresent()) {
                    return ResponseEntity.ok(tokensOptional.get());
                } else {
                    throw new OAuth2TokenException(TokenResponseError.from(ErrorType.access_denied, "Access denied."));
                }
            } else if (GrantType.PASSWORD.equals(grantTypeEnum)) {
                Optional<ClientCredentials> ccOptional = getClientCredentials(request, clientId, clientSecret);
                if (ccOptional.isPresent()) {
                    ClientCredentials clientCredentials = ccOptional.get();
                    LOG.info("postGetTokens: grantType={} username={} scope={} clientId={}", grantType, username, scope, clientCredentials.getId().getId());
                    Scope scopes = ModelUtils.getScopes(scope);
                    UPAuthenticationRequest upAuthenticationRequest = new UPAuthenticationRequest(UserId.from(username), password, scopes, clientCredentials);
                    Optional<TokenResponse> tokensOptional = authenticationService.authenticate(issuerUri, orgId, projId, clientCredentials, scopes, upAuthenticationRequest, idTokenRequest);
                    if (tokensOptional.isPresent()) {
                        return ResponseEntity.ok(tokensOptional.get());
                    } else {
                        throw new OAuth2TokenException(TokenResponseError.from(ErrorType.access_denied, "Access denied."));
                    }
                } else {
                    LOG.warn("Can't get client credentials !");
                    throw new OAuth2TokenException(TokenResponseError.from(ErrorType.unauthorized_client, "Can't get client credentials !"));
                }
            } else if (GrantType.CLIENT_CREDENTIALS.equals(grantTypeEnum)) {
                Optional<ClientCredentials> ccOptional = getClientCredentials(request, clientId, clientSecret);
                if (ccOptional.isPresent()) {
                    ClientCredentials clientCredentials = ccOptional.get();
                    LOG.info("postGetTokens: grantType={} scope={} clientId={}", grantType, scope, clientCredentials.getId().getId());
                    Scope scopes = ModelUtils.getScopes(scope);
                    Optional<TokenResponse> tokensOptional = authenticationService.authenticate(issuerUri, orgId, projId, clientCredentials, scopes, idTokenRequest);
                    if (tokensOptional.isPresent()) {
                        return ResponseEntity.ok(tokensOptional.get());
                    } else {
                        throw new OAuth2TokenException(TokenResponseError.from(ErrorType.access_denied, "Access denied."));
                    }
                } else {
                    LOG.warn("Can't get client credentials !");
                    throw new OAuth2TokenException(TokenResponseError.from(ErrorType.unauthorized_client, "Can't get client credentials !"));
                }
            } else if (GrantType.REFRESH_TOKEN.equals(grantTypeEnum)) {
                Optional<ClientCredentials> ccOptional = getClientCredentials(request, clientId, clientSecret);
                if (ccOptional.isPresent()) {
                    ClientCredentials clientCredentials = ccOptional.get();
                    LOG.info("postGetTokens: grantType={} scope={} clientId={} refreshToken={}", grantType, scope, clientCredentials.getId().getId(), refreshToken);
                    JWToken jwToken = new JWToken(refreshToken);
                    Scope scopes = ModelUtils.getScopes(scope);
                    Optional<TokenResponse> tokensOptional = authenticationService.refreshTokens(orgId, projId, jwToken, clientCredentials, scopes, idTokenRequest);
                    if (tokensOptional.isPresent()) {
                        return ResponseEntity.ok(tokensOptional.get());
                    } else {
                        throw new OAuth2TokenException(TokenResponseError.from(ErrorType.access_denied, "Access denied."));
                    }
                } else {
                    LOG.warn("Can't get client credentials !");
                    throw new OAuth2TokenException(TokenResponseError.from(ErrorType.unauthorized_client, "Can't get client credentials !"));
                }
            } else {
                LOG.warn("Unsupported grant_type={} !", grantType);
                throw new OAuth2TokenException(TokenResponseError.from(ErrorType.invalid_request, "Unsupported grant_type=", grantType));
            }
        } catch (URISyntaxException e) {
            throw new OAuth2TokenException(e, TokenResponseError.from(ErrorType.invalid_request, "Callback URI invalid syntax."));
        } catch (MalformedURLException e) {
            throw new OAuth2TokenException(e, TokenResponseError.from(ErrorType.invalid_request, "Callback URI malformed."));
        }
    }

    @Operation(description = "__Start Authorization Code Grant flow__ \n" +
            "This endpoint starts Authorization flow by serving login page.\n" +
            "- [Authorization Code Grant flow](https://tools.ietf.org/html/rfc6749#section-4.1.1) \n" +
            "- [Implicit Grant flow](https://tools.ietf.org/html/rfc6749#section-4.2.1) \n")
    @GetMapping(path = "/{organization-id}/{project-id}/authorize", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getAuthorize(@PathVariable("organization-id") String organizationId,
                                               @PathVariable("project-id") String projectId,
                                               @RequestParam(name = "response_type") String responseType,
                                               @RequestParam(name = "client_id") String clientId,
                                               @RequestParam(name = "redirect_uri") String redirectUri,
                                               @RequestParam(name = "state") String state,
                                               @RequestParam(name = "scope", required = false) String scope,
                                               @RequestParam(name = "code_challenge", required = false) String codeChallenge,
                                               @RequestParam(name = "code_challenge_method", required = false) String codeChallengeMethod,
                                               HttpServletRequest request) {
        LOG.info("getAuthorize: {}?{}", request.getRequestURL(), request.getQueryString());
        LOG.info("getAuthorize: {}/{} responseType={} clientId={} redirectUri={} state={} scope={}", organizationId, projectId, responseType, clientId, redirectUri, state, scope);
        if (codeChallenge != null) {
            LOG.info("getAuthorize: PKCE code_challenge={} code_challenge_method={}", codeChallenge, codeChallengeMethod);
        }
        if (scope == null) {
            scope = "";
        }
        if (codeChallenge == null) {
            codeChallenge = "";
        }
        if (codeChallengeMethod == null) {
            codeChallengeMethod = "PLAIN";
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
        result = result.replace("__code_challenge__", codeChallenge);
        result = result.replace("__code_challenge_method__", codeChallengeMethod);
        result = result.replace("__random__", UUID.randomUUID().toString()); //to prevent form caching
        return ResponseEntity.ok(result);
    }

    @Operation(description = "This endpoint performs programmatic authorization of end-user using Authorization Code Grant flow.")
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
                authorizationCodeGrantRequest.getRedirectUri(),
                authorizationCodeGrantRequest.getCodeChallenge(), authorizationCodeGrantRequest.getCodeChallengeMethod());
        return ResponseEntity.of(authorizationCode);
    }

    @Operation(description = "This endpoint performs programmatic consent confirmation for end-user using Authorization Code Grant flow.")
    @PostMapping(path = "/{organization-id}/{project-id}/consent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> consentProgrammatically(@PathVariable("organization-id") String organizationId,
                                                        @PathVariable("project-id") String projectId,
                                                        @RequestBody ConsentRequest request) throws URISyntaxException {
        LOG.info("consentProgrammatically: {}/{}", organizationId, projectId);
        Scope scopes = new Scope(Set.copyOf(request.getScopes()));
        Optional<AuthorizationCodeContext> authorizationCodeContext = authenticationService.setScope(request.getCode(), scopes);
        if (authorizationCodeContext.isPresent()) {
            // DO NOT redirect here ! User-Agent (Browser) will perform redirection.
            // Redirection is done here static/login-form.js#onConsentOk()
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(description = "Default redirect endpoint, only for testing purposes.")
    @GetMapping(path = "/{organization-id}/{project-id}/redirect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> redirect(@PathVariable("organization-id") String organizationId,
                                                  @PathVariable("project-id") String projectId,
                                                  @RequestParam("code") String code,
                                                  @RequestParam("state") String state,
                                                  @RequestBody MultiValueMap bodyValueMap,
                                                  HttpServletRequest request) throws URISyntaxException, MalformedURLException {
        String codeVerifier = getCodeVerifier(bodyValueMap);
        LOG.info("default redirect: {}/{} code={} state={}", organizationId, projectId, code, state);
        LOG.info("default redirect: codeVerifier={}", codeVerifier);
        RestTemplate restTemplate = new RestTemplate();
        URI issuerUri = getIssuerUri(servletContext, request, organizationId, projectId);
        String tokenUrl = issuerUri.toString() + "/token" + "?grant_type=authorization_code&code=" + code + "&state=" + state;
        //TODO: replace with OKHTTP3
        ResponseEntity<TokenResponse> tokenResponseResponseEntity = restTemplate.postForEntity(tokenUrl, null, TokenResponse.class);
        if (HttpStatus.OK.equals(tokenResponseResponseEntity.getStatusCode())) {
            return ResponseEntity.ok(tokenResponseResponseEntity.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(description = "__OpenID Connect Discovery__ \n" +
            "Get information about this OAuth2 server configuration. \n" +
            "- [OpenID Connect Discovery](https://openid.net/specs/openid-connect-discovery-1_0.html)")
    @GetMapping(path = "/{organization-id}/{project-id}/.well-known/openid-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProviderConfigurationResponse> getConfiguration(@PathVariable("organization-id") String organizationId,
                                                                          @PathVariable("project-id") String projectId,
                                                                          HttpServletRequest request) throws MalformedURLException {
        LOG.info("getConfiguration: {}", request.getRequestURL());
        String baseUrl = getBaseUrl(servletContext, request, baseUrlMapper);
        ProviderConfigurationRequest providerConfigurationRequest = new ProviderConfigurationRequest(baseUrl, OrganizationId.from(organizationId), ProjectId.from(projectId));
        ProviderConfigurationResponse configuration = providerConfigurationService.getConfiguration(providerConfigurationRequest);
        return ResponseEntity.ok(configuration);
    }

    @Operation(description = "__Get JSON Web Keys (JWK)__ \n" +
            "Get all available public keys on the project. A JSON Web Key (JWK) is a JavaScript Object Notation (JSON) data\n" +
            "structure that represents a cryptographic key. \n" +
            "- [RFC7517](https://tools.ietf.org/html/rfc7517)")
    @GetMapping(path = "/{organization-id}/{project-id}/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JWKResponse> getCerts(@PathVariable("organization-id") String organizationId,
                                                @PathVariable("project-id") String projectId) {
        LOG.info("getCerts: organizationId={} projectId={}", organizationId, projectId);
        JWKResponse jwkData = providerConfigurationService.getJWKData(OrganizationId.from(organizationId), ProjectId.from(projectId));
        return ResponseEntity.ok(jwkData);
    }

    @Operation(description = "__OAuth 2.0 Token Introspection__ \n" +
            "Get active state about provided token." +
            "- [RFC7662](https://tools.ietf.org/html/rfc7662)")
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

    @Operation(description = "__OAuth 2.0 Token Revocation__ \n" +
            "Revoke issued and valid token which is no longer needed." +
            "- [RFC7009](https://tools.ietf.org/html/rfc7009)")
    @PostMapping(path = "/{organization-id}/{project-id}/revoke", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> revoke(@PathVariable("organization-id") String organizationId,
                                       @PathVariable("project-id") String projectId,
                                       @RequestParam("token") String token,
                                       @RequestParam(name = "token_type_hint", required = false) String tokenTypeHint) {
        RevokeTokenRequest request = new RevokeTokenRequest(JWToken.from(token), ControllerUtils.getTokenType(tokenTypeHint));
        authenticationService.revoke(OrganizationId.from(organizationId), ProjectId.from(projectId), request);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "__UserInfo Request__ \n" +
            "Get info about User\n" +
            "- [OICD/UserInfoRequest](https://openid.net/specs/openid-connect-core-1_0.html#UserInfoRequest)")
    @GetMapping(path = "/{organization-id}/{project-id}/userinfo", produces = MediaType.APPLICATION_JSON_VALUE )
    @PostMapping(path = "/{organization-id}/{project-id}/userinfo", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable("organization-id") String organizationId,
                                                        @PathVariable("project-id") String projectId,
                                                        HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            String token = authorization.substring(BEARER_PREFIX.length());
            Optional<UserInfoResponse> response = authenticationService.getUserInfo(OrganizationId.from(organizationId), ProjectId.from(projectId), JWToken.from(token));
            return ResponseEntity.of(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
