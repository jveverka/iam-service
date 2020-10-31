package itx.iamservice.client.spring;

import itx.iamservice.client.JWTUtils;
import itx.iamservice.client.dto.StandardTokenClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class IAMSecurityFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(IAMSecurityFilter.class);

    private final IAMSecurityFilterConfiguration configuration;

    public IAMSecurityFilter(@Autowired IAMSecurityFilterConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(JWTUtils.AUTHORIZATION);
        if (authorization != null) {
            Optional<StandardTokenClaims> claimSetOptional = configuration.getIamClient().validate(JWTUtils.extractJwtToken(authorization));
            if (claimSetOptional.isPresent()) {
                StandardTokenClaims standardTokenClaims = claimSetOptional.get();
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(new AuthenticationImpl(standardTokenClaims));
                filterChain.doFilter(request, response);
                return;
            } else {
                LOG.error("IAMSecurity: Token validation has failed !");
            }
        } else {
            LOG.error("IAMSecurity: header \"Authorization\" is missing !");
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return configuration.getExcludeEndpoints().stream()
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
    }

}
