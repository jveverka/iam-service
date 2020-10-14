package itx.iamservice.server.utils;

import itx.iamservice.core.model.TokenType;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

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

    public static String getBaseUrl(ServletContext servletContext, HttpServletRequest request) throws MalformedURLException {
        String contextPath = getContextPath(servletContext);
        URL url = new URL(request.getRequestURL().toString());
        return url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + contextPath + "/services/authentication";
    }

    public static URI getIssuerUri(ServletContext servletContext, HttpServletRequest request, String organizationId, String projectId) throws URISyntaxException, MalformedURLException {
        String contextPath = getContextPath(servletContext);
        URL url = new URL(request.getRequestURL().toString());
        if (projectId != null) {
            return new URI(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + contextPath + "/services/authentication/" + organizationId + "/" + projectId);
        } else {
            return new URI(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + contextPath + "/services/authentication/" + organizationId);
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

}
