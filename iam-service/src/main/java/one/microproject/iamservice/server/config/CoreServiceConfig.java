package one.microproject.iamservice.server.config;

import one.microproject.iamservice.core.services.AuthenticationService;
import one.microproject.iamservice.core.services.ProviderConfigurationService;
import one.microproject.iamservice.core.services.TokenGenerator;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.services.admin.ClientManagementService;
import one.microproject.iamservice.core.services.caches.AuthorizationCodeCache;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.caches.TokenCache;
import one.microproject.iamservice.core.services.ResourceServerService;
import one.microproject.iamservice.core.services.admin.UserManagerService;
import one.microproject.iamservice.core.services.admin.OrganizationManagerService;
import one.microproject.iamservice.core.services.admin.ProjectManagerService;
import one.microproject.iamservice.core.services.impl.AuthenticationServiceImpl;
import one.microproject.iamservice.core.services.impl.ProviderConfigurationServiceImpl;
import one.microproject.iamservice.core.services.impl.ResourceServerServiceImpl;
import one.microproject.iamservice.core.services.impl.TokenGeneratorImpl;
import one.microproject.iamservice.client.impl.TokenValidatorImpl;
import one.microproject.iamservice.core.services.impl.admin.ClientManagementServiceImpl;
import one.microproject.iamservice.core.services.impl.admin.UserManagerServiceImpl;
import one.microproject.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import one.microproject.iamservice.core.services.impl.admin.ProjectManagerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CoreServiceConfig {

    @Bean
    @Scope("singleton")
    public TokenValidator getTokenValidator()  {
        return new TokenValidatorImpl();
    }

    @Bean
    @Scope("singleton")
    public TokenGenerator getTokenGenerator() {
        return new TokenGeneratorImpl();
    }

    @Bean
    @Scope("singleton")
    public AuthenticationService getAuthenticationService(@Autowired ModelCache modelCache,
                                                          @Autowired TokenCache tokenCache,
                                                          @Autowired AuthorizationCodeCache authorizationCodeCache,
                                                          @Autowired TokenGenerator tokenGenerator,
                                                          @Autowired TokenValidator tokenValidator) {
        return new AuthenticationServiceImpl(modelCache, tokenCache, authorizationCodeCache, tokenGenerator, tokenValidator);
    }

    @Bean
    @Scope("singleton")
    public ResourceServerService getResourceServerService(@Autowired ModelCache modelCache,
                                                          @Autowired TokenCache tokenCache,
                                                          @Autowired TokenValidator tokenValidator) {
        return new ResourceServerServiceImpl(modelCache, tokenCache, tokenValidator);
    }

    @Bean
    @Scope("singleton")
    public OrganizationManagerService getOrganizationManagerService(@Autowired ModelCache modelCache) {
        return new OrganizationManagerServiceImpl(modelCache);
    }

    @Bean
    @Scope("singleton")
    public ProjectManagerService getProjectManagerService(@Autowired ModelCache modelCache) {
        return new ProjectManagerServiceImpl(modelCache);
    }

    @Bean
    @Scope("singleton")
    public UserManagerService getClientManagerService(@Autowired ModelCache modelCache) {
        return new UserManagerServiceImpl(modelCache);
    }

    @Bean
    @Scope("singleton")
    public ClientManagementService getClientManagementService(@Autowired ModelCache modelCache) {
        return new ClientManagementServiceImpl(modelCache);
    }

    @Bean
    @Scope("singleton")
    public ProviderConfigurationService getProviderConfigurationService(@Autowired OrganizationManagerService organizationManagerService,
                                                                        @Autowired ProjectManagerService projectManagerService) {
        return new ProviderConfigurationServiceImpl(organizationManagerService, projectManagerService);
    }

}
