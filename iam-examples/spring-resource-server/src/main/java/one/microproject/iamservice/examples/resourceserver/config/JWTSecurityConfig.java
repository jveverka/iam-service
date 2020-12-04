package one.microproject.iamservice.examples.resourceserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class JWTSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authz -> authz
                        .antMatchers(HttpMethod.GET, "/services/secure/**").hasAuthority("SCOPE_iam-admins-iam-admins.users.all")
                        .antMatchers(HttpMethod.POST, "/services/secure/**").hasAuthority("SCOPE_iam-admins-iam-admins.users.all")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .cors().disable();
    }

}
