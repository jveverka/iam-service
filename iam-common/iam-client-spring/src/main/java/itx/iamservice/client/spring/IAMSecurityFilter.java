package itx.iamservice.client.spring;

import com.nimbusds.jwt.JWTClaimsSet;
import itx.iamservice.client.IAMClient;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IAMSecurityFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(IAMSecurityFilter.class);

    private final IAMClient iamClient;

    public IAMSecurityFilter(IAMClient iamClient) {
        this.iamClient = iamClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization != null) {
            Optional<JWTClaimsSet> claimSet = iamClient.validate(JWTUtils.extractJwtToken(authorization));
            if (claimSet.isPresent()) {
                SecurityContext securityContext = SecurityContextHolder.getContext();
                String userIdFromJWT = claimSet.get().getSubject();
                List<String> permissionsFromJWT = (List<String>) claimSet.get().getClaim("permissions");
                Set<String> setOfPermissions = permissionsFromJWT.stream().map(p  -> "ROLE_" + p).collect(Collectors.toSet());
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
