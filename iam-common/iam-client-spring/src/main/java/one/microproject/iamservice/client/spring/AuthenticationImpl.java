package one.microproject.iamservice.client.spring;

import one.microproject.iamservice.client.dto.StandardTokenClaims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AuthenticationImpl implements Authentication {

    private final String name;
    private final Collection<GrantedAuthorityImpl> grantedAuthorities;
    private final StandardTokenClaims standardTokenClaims;

    private boolean isAuthenticated;

    public AuthenticationImpl(StandardTokenClaims standardTokenClaims) {
        this.name = standardTokenClaims.getSubject();
        List<GrantedAuthorityImpl> authorities = new ArrayList<>();
        standardTokenClaims.getScope().forEach(r->
            authorities.add(new GrantedAuthorityImpl(r))
        );
        this.grantedAuthorities = Collections.unmodifiableCollection(authorities);
        this.isAuthenticated = true;
        this.standardTokenClaims = standardTokenClaims;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return standardTokenClaims;
    }

    @Override
    public Object getPrincipal() {
        return name;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return name;
    }

}
