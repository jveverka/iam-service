package itx.iamservice.server.config.security;

import itx.iamservice.server.services.IAMSecurityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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
                .addFilterBefore(new ProjectManagementSecurityFilter(), BasicAuthenticationFilter.class)
                .antMatcher("/services/management/**")
                .addFilterBefore(new AdminSecurityFilter(iamSecurityValidator), BasicAuthenticationFilter.class)
                .antMatcher("/services/admin/**")
                .csrf()
                .disable();
    }

}
