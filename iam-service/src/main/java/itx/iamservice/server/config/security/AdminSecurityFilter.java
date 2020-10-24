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


/**
 * This filter validates authorization header (Bearer JWT token) and validate IAM Admin permission set.
 * If token validation is successful, content of JWT token is mapped to Security Context {@link AuthenticationImpl}.
 */
public class AdminSecurityFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AdminSecurityFilter.class);

    private final IAMSecurityValidator iamSecurityValidator;

    public AdminSecurityFilter(IAMSecurityValidator iamSecurityValidator) {
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        String requestUrl = httpServletRequest.getRequestURL().toString();
        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization != null) {
            try {
                LOG.info("doFilter: {} {}", requestUrl, authorization);
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
    }

}
