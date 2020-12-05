package one.microproject.iamservice.server.config.security;

import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.client.spring.AuthenticationImpl;
import one.microproject.iamservice.server.services.IAMSecurityException;
import one.microproject.iamservice.server.services.IAMSecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class IAMSecurityFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(IAMSecurityFilter.class);

    private final IAMSecurityValidator iamSecurityValidator;

    public IAMSecurityFilter(@Autowired IAMSecurityValidator iamSecurityValidator) {
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String requestUrl = httpServletRequest.getRequestURL().toString();
        String requestUri =  httpServletRequest.getRequestURI();
        if (requestUri.startsWith("/services/admin/")) {
            String authorization = httpServletRequest.getHeader("Authorization");
            if (authorization != null) {
                try {
                    LOG.info("doAdminFilter: {} {} {}", requestUri, requestUrl, authorization);
                    StandardTokenClaims standardTokenClaims = iamSecurityValidator.verifyAdminAccess(authorization);
                    SecurityContextHolder.getContext().setAuthentication(new AuthenticationImpl(standardTokenClaims));
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
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
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
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
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

}
