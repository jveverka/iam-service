package one.microproject.iamservice.server.controller.support;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.model.ClientCredentials;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.iamservice.server.services.BaseUrlMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Optional;

import static one.microproject.iamservice.client.JWTUtils.AUTHORIZATION;

public final class ControllerUtils {

    private ControllerUtils() {
    }

    public static String getContextPath(ServletContext servletContext) {
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

    public static String getBaseUrl(ServletContext servletContext, HttpServletRequest request, BaseUrlMapper baseUrlMapper) throws MalformedURLException {
        String contextPath = getContextPath(servletContext);
        URL url = new URL(request.getRequestURL().toString());
        String baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + contextPath;
        baseUrl = baseUrlMapper.mapIfEquals(baseUrl);
        return baseUrl + "/services/oauth2";
    }

    public static URI getIssuerUri(ServletContext servletContext, HttpServletRequest request, String organizationId, String projectId, BaseUrlMapper baseUrlMapper) throws URISyntaxException, MalformedURLException {
        String contextPath = getContextPath(servletContext);
        URL url = new URL(request.getRequestURL().toString());
        String baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + contextPath;
        baseUrl = baseUrlMapper.mapIfEquals(baseUrl);
        if (projectId != null) {
            return new URI(baseUrl + "/services/oauth2/" + organizationId + "/" + projectId);
        } else {
            return new URI(baseUrl + "/services/oauth2/" + organizationId);
        }
    }


    public static TokenType getTokenType(String tokenTypeHint) {
        if (tokenTypeHint == null) {
            return null;
        } else {
            return TokenType.getTokenType(tokenTypeHint);
        }
    }

    public static String getParameters(Enumeration<String> parameters) {
        StringBuilder sb = new StringBuilder();
        while (parameters.hasMoreElements()) {
            sb.append(parameters.nextElement());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    public static Optional<ClientCredentials> getClientCredentials(HttpServletRequest request, String clientId, String clientSecret) {
        if (clientId != null && clientSecret != null) {
            return Optional.of(new ClientCredentials(ClientId.from(clientId), clientSecret));
        }
        String authorization = request.getHeader(AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Basic ")) {
            String[] authorizations = authorization.split(" ");
            byte[] decoded = Base64.getDecoder().decode(authorizations[1]);
            String decodedString = new String(decoded);
            String[] usernamePassword = decodedString.split(":");
            return Optional.of(new ClientCredentials(ClientId.from(usernamePassword[0]), usernamePassword[1]));
        }
        return Optional.empty();
    }

    public static String getCodeVerifier(MultiValueMap bodyValueMap) {
        if (bodyValueMap == null) {
            return null;
        }
        Object codeVerifier = bodyValueMap.getFirst("code_verifier");
        if (codeVerifier == null) {
            return null;
        } else {
            return codeVerifier.toString();
        }
    }

    public static Optional<TokenResponse> getResponse(URI issuerUri, String code, String state) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = issuerUri.toString() + "/token" + "?grant_type=authorization_code&code=" + code + "&state=" + state;
        //TODO: replace with OKHTTP3
        ResponseEntity<TokenResponse> tokenResponseResponseEntity = restTemplate.postForEntity(tokenUrl, null, TokenResponse.class);
        if (HttpStatus.OK.equals(tokenResponseResponseEntity.getStatusCode())) {
            return Optional.ofNullable(tokenResponseResponseEntity.getBody());
        } else {
            return Optional.empty();
        }
    }

}
