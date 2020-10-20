package itx.iamservice.server.config.security;

import itx.iamservice.client.dto.StandardTokenClaims;
import itx.iamservice.client.spring.AuthenticationImpl;
import itx.iamservice.server.services.IAMSecurityException;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        String contextPath = httpServletRequest.getContextPath();
        String requestUrl = httpServletRequest.getRequestURL().toString();
        String authorization = httpServletRequest.getHeader("Authorization");
        LOG.info("doFilter: {} {} {}", contextPath, requestUrl, authorization);
        if (authorization != null) {
            try {
                StandardTokenClaims standardTokenClaims = iamSecurityValidator.validate(authorization);
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