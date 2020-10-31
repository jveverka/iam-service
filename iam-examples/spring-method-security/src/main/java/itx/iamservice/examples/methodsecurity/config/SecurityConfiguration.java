package itx.iamservice.examples.methodsecurity.config;

import itx.iamservice.client.spring.IAMSecurityFilter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;
import java.security.Security;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

    private final IAMSecurityFilter iamSecurityFilter;

    @Autowired
    public SecurityConfiguration(IAMSecurityFilter iamSecurityFilter) {
        this.iamSecurityFilter = iamSecurityFilter;
    }

    @PostConstruct
    private void init() {
        LOG.info("#CONFIG: initializing Bouncy Castle Provider (BCP) ...");
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers("/services/public/**").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated();

        httpSecurity.addFilterBefore(iamSecurityFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
