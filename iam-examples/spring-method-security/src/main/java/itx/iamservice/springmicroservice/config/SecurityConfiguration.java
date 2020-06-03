package itx.iamservice.springmicroservice.config;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.spring.IAMSecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final IAMClient iamClient;

    @Autowired
    public SecurityConfiguration(IAMClient iamClient) {
        this.iamClient = iamClient;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(new IAMSecurityFilter(iamClient), BasicAuthenticationFilter.class)
                .antMatcher("/services/data/**")
                .csrf()
                .disable();
    }

}
