package itx.iamservice.server.config.security;

import itx.iamservice.server.services.IAMSecurityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final IAMSecurityValidator iamSecurityValidator;

    public SecurityConfiguration(@Autowired IAMSecurityValidator iamSecurityValidator) {
        this.iamSecurityValidator = iamSecurityValidator;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterAfter(new IAMSecurityFilter(iamSecurityValidator), SecurityContextPersistenceFilter.class)
                .antMatcher("/services/**")
                .csrf()
                .disable();
    }

}
