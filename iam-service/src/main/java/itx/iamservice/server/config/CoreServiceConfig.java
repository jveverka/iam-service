package itx.iamservice.server.config;

import itx.iamservice.core.services.AuthenticationService;
import itx.iamservice.core.services.ProviderConfigurationService;
import itx.iamservice.core.services.admin.ClientManagementService;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.impl.AuthenticationServiceImpl;
import itx.iamservice.core.services.impl.ProviderConfigurationServiceImpl;
import itx.iamservice.core.services.impl.ResourceServerServiceImpl;
import itx.iamservice.core.services.impl.admin.ClientManagementServiceImpl;
import itx.iamservice.core.services.impl.admin.UserManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.ProjectManagerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CoreServiceConfig {

    private final ModelCache modelCache;
    private final TokenCache tokenCache;
    private final AuthorizationCodeCache authorizationCodeCache;

    public CoreServiceConfig(@Autowired ModelCache modelCache,
                             @Autowired TokenCache tokenCache,
                             @Autowired AuthorizationCodeCache authorizationCodeCache) {
        this.modelCache = modelCache;
        this.tokenCache = tokenCache;
        this.authorizationCodeCache = authorizationCodeCache;
    }

    @Bean
    @Scope("singleton")
    public AuthenticationService getAuthenticationService() {
        return new AuthenticationServiceImpl(modelCache, tokenCache, authorizationCodeCache);
    }

    @Bean
    @Scope("singleton")
    public ResourceServerService getResourceServerService() {
        return new ResourceServerServiceImpl(modelCache, tokenCache);
    }

    @Bean
    @Scope("singleton")
    public OrganizationManagerService getOrganizationManagerService() {
        return new OrganizationManagerServiceImpl(modelCache);
    }

    @Bean
    @Scope("singleton")
    public ProjectManagerService getProjectManagerService() {
        return new ProjectManagerServiceImpl(modelCache);
    }

    @Bean
    @Scope("singleton")
    public UserManagerService getClientManagerService() {
        return new UserManagerServiceImpl(modelCache);
    }

    @Bean
    @Scope("singleton")
    public ClientManagementService getClientManagementService() {
        return new ClientManagementServiceImpl(modelCache);
    }

    @Bean
    @Scope("singleton")
    public ProviderConfigurationService getProviderConfigurationService(@Autowired OrganizationManagerService organizationManagerService,
                                                                        @Autowired ProjectManagerService projectManagerService) {
        return new ProviderConfigurationServiceImpl(organizationManagerService, projectManagerService);
    }

}
