package itx.iamservice.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    public SecurityConfiguration() {
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(new ManagementSecurityFilter(), BasicAuthenticationFilter.class)
                .antMatcher("/services/management/**")
                .addFilterBefore(new AdminSecurityFilter(), BasicAuthenticationFilter.class)
                .antMatcher("/services/admin/**")
                .csrf()
                .disable();
    }

}
