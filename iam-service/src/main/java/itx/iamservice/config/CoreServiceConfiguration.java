package itx.iamservice.config;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.impl.ClientServiceImpl;
import itx.iamservice.core.services.impl.ResourceServerServiceImpl;
import itx.iamservice.core.services.impl.admin.UserManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.ProjectManagerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CoreServiceConfiguration {

    private final Model model;
    private final TokenCache tokenCache;

    public CoreServiceConfiguration(@Autowired Model model,
                                    @Autowired TokenCache tokenCache) {
        this.model = model;
        this.tokenCache = tokenCache;
    }

    @Bean
    @Scope("singleton")
    public ClientService getClientService() {
        return new ClientServiceImpl(model, tokenCache);
    }

    @Bean
    @Scope("singleton")
    public ResourceServerService getResourceServerService() {
        return new ResourceServerServiceImpl(model, tokenCache);
    }

    @Bean
    @Scope("singleton")
    public OrganizationManagerService getOrganizationManagerService() {
        return new OrganizationManagerServiceImpl(model);
    }

    @Bean
    @Scope("singleton")
    public ProjectManagerService getProjectManagerService() {
        return new ProjectManagerServiceImpl(model);
    }

    @Bean
    @Scope("singleton")
    public UserManagerService getClientManagerService() {
        return new UserManagerServiceImpl(model);
    }

}
