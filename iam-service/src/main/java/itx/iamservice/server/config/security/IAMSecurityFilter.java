package itx.iamservice.server.config.security;

import itx.iamservice.client.dto.StandardTokenClaims;
import itx.iamservice.client.spring.AuthenticationImpl;
import itx.iamservice.server.services.IAMSecurityException;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class IAMSecurityFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(IAMSecurityFilter.class);

    private final IAMSecurityValidator iamSecurityValidator;

    public IAMSecurityFilter(IAMSecurityValidator iamSecurityValidator) {
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        String requestUrl = httpServletRequest.getRequestURL().toString();
        String requestUri =  httpServletRequest.getRequestURI();
        if (requestUri.startsWith("/services/admin/")) {
            String authorization = httpServletRequest.getHeader("Authorization");
            if (authorization != null) {
                try {
                    LOG.info("doAdminFilter: {} {} {}", requestUri, requestUrl, authorization);
                    StandardTokenClaims standardTokenClaims = iamSecurityValidator.verifyAdminAccess(authorization);
                    SecurityContextHolder.getContext().setAuthentication(new AuthenticationImpl(standardTokenClaims));
                    chain.doFilter(request, response);
                } catch (IAMSecurityException iamSecurityException) {
                    LOG.info("Unauthorized: invalid Authorization token !");
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                LOG.info("Unauthorized: missing Authorization token !");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        } else if  (requestUri.startsWith("/services/management/")) {
            String authorization = httpServletRequest.getHeader("Authorization");
            if (authorization != null) {
                try {
                    LOG.info("doFilter: {} {} {}", requestUri, requestUrl, authorization);
                    StandardTokenClaims standardTokenClaims = iamSecurityValidator.verifyToken(authorization);
                    SecurityContextHolder.getContext().setAuthentication(new AuthenticationImpl(standardTokenClaims));
                    chain.doFilter(request, response);
                } catch (IAMSecurityException iamSecurityException) {
                    LOG.info("Unauthorized: invalid Authorization token !");
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                LOG.info("Unauthorized: missing Authorization token !");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        } else {
            chain.doFilter(request, response);
        }
    }

}
