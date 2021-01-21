package one.microproject.iamservice.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final IAMSecurityFilter iamSecurityFilter;

    public SecurityConfiguration(@Autowired IAMSecurityFilter iamSecurityFilter) {
        this.iamSecurityFilter = iamSecurityFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterAfter(iamSecurityFilter, SecurityContextPersistenceFilter.class)
                .antMatcher("/services/**")
                .csrf()
                .ignoringAntMatchers("/services/**");
                //.disable();
    }

}
