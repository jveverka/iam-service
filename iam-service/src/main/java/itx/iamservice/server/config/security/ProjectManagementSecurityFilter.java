package itx.iamservice.server.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ProjectManagementSecurityFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectManagementSecurityFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String contextPath = httpServletRequest.getContextPath();
        String requestUrl = httpServletRequest.getRequestURL().toString();
        String authorization = httpServletRequest.getHeader("Authorization");
        LOG.info("doFilter: {}/{} {}", contextPath, requestUrl, authorization);
        chain.doFilter(request, response);
    }

}
