package itx.iamservice.client.spring;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.JWTUtils;
import itx.iamservice.client.dto.StandardTokenClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class IAMSecurityFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(IAMSecurityFilter.class);

    private static final String ROLE_PREFIX = "ROLE_";

    private final IAMClient iamClient;

    public IAMSecurityFilter(IAMClient iamClient) {
        this.iamClient = iamClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String authorization = httpServletRequest.getHeader(JWTUtils.AUTHORIZATION);
        if (authorization != null) {
            Optional<StandardTokenClaims> claimSetOptional = iamClient.validate(JWTUtils.extractJwtToken(authorization));
            if (claimSetOptional.isPresent()) {
                StandardTokenClaims standardTokenClaims = claimSetOptional.get();
                SecurityContext securityContext = SecurityContextHolder.getContext();
                String userIdFromJWT = standardTokenClaims.getSubject();
                Set<String> setOfPermissions = new HashSet<>();
                for (String scope: standardTokenClaims.getAudience()) {
                    setOfPermissions.add(ROLE_PREFIX + scope);
                }
                securityContext.setAuthentication(new AuthenticationImpl(userIdFromJWT, setOfPermissions));
                chain.doFilter(request, response);
                return;
            }
        } else {
            LOG.error("not authorized: header \"Authorization\" is missing !");
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
    }

}
