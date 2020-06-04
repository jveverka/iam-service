package itx.iamservice.server.config;

import itx.iamservice.core.services.AuthenticationService;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.impl.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AuthenticationServiceConfig {

    private final ClientService clientService;

    public AuthenticationServiceConfig(@Autowired ClientService clientService) {
        this.clientService = clientService;
    }

    @Bean
    @Scope("singleton")
    public AuthenticationService getAuthenticationService() {
        return new AuthenticationServiceImpl(clientService);
    }

}
