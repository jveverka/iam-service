package itx.iamservice.core;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.AuthenticationService;
import itx.iamservice.core.services.ClientService;
import itx.iamservice.core.services.ProviderConfigurationService;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.admin.ClientManagementService;
import itx.iamservice.core.services.admin.OrganizationManagerService;
import itx.iamservice.core.services.admin.ProjectManagerService;
import itx.iamservice.core.services.admin.UserManagerService;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.caches.CacheCleanupScheduler;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.caches.TokenCache;
import itx.iamservice.core.services.impl.AuthenticationServiceImpl;
import itx.iamservice.core.services.impl.ClientServiceImpl;
import itx.iamservice.core.services.impl.caches.ModelCacheImpl;
import itx.iamservice.core.services.impl.ProviderConfigurationServiceImpl;
import itx.iamservice.core.services.impl.ResourceServerServiceImpl;
import itx.iamservice.core.services.impl.admin.ClientManagementServiceImpl;
import itx.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.ProjectManagerServiceImpl;
import itx.iamservice.core.services.impl.admin.UserManagerServiceImpl;
import itx.iamservice.core.services.impl.caches.AuthorizationCodeCacheImpl;
import itx.iamservice.core.services.impl.caches.CacheCleanupSchedulerImpl;
import itx.iamservice.core.services.impl.caches.TokenCacheImpl;
import itx.iamservice.core.services.impl.persistence.InMemoryPersistenceServiceImpl;
import itx.iamservice.core.services.persistence.PersistenceService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.concurrent.TimeUnit;

public class IAMCoreBuilder {

    private Model model;
    private ModelCache modelCache;
    private PersistenceService persistenceService;
    private CacheCleanupScheduler cacheCleanupScheduler;
    private AuthorizationCodeCache authorizationCodeCache;
    private TokenCache tokenCache;
    private AuthenticationService authenticationService;
    private ClientService clientService;
    private ResourceServerService resourceServerService;
    private ClientManagementService clientManagementService;
    private OrganizationManagerService organizationManagerService;
    private ProjectManagerService projectManagerService;
    private UserManagerService userManagerService;
    private ProviderConfigurationService providerConfigurationService;

    public IAMCoreBuilder withBCProvider() {
        Security.addProvider(new BouncyCastleProvider());
        return this;
    }

    public IAMCoreBuilder withModel(Model model, PersistenceService persistenceService) {
        this.model = model;
        this.persistenceService = persistenceService;
        this.modelCache = new ModelCacheImpl(model, persistenceService);
        return this;
    }

    public IAMCoreBuilder withModel(Model model) {
        this.model = model;
        this.persistenceService = new InMemoryPersistenceServiceImpl();
        this.modelCache = new ModelCacheImpl(model, this.persistenceService);
        return this;
    }

    public IAMCoreBuilder withDefaultModel(String iamAdminPassword) throws PKIException {
        this.modelCache = ModelUtils.createDefaultModelCache(iamAdminPassword);
        this.model = modelCache.getModel();
        return this;
    }

    public IAMCoreBuilder withAuthorizationCodeCache(AuthorizationCodeCache authorizationCodeCache) {
        this.authorizationCodeCache = authorizationCodeCache;
        return this;
    }

    public IAMCoreBuilder withDefaultAuthorizationCodeCache(Long maxDuration, TimeUnit timeUnit) {
        this.authorizationCodeCache = new AuthorizationCodeCacheImpl(maxDuration, timeUnit);
        return this;
    }

    public IAMCoreBuilder withTokenCache(TokenCache tokenCache) {
        this.tokenCache = tokenCache;
        return this;
    }

    public IAMCoreBuilder withDefaultTokenCache() {
        this.tokenCache = new TokenCacheImpl(modelCache);
        return this;
    }

    public IAMCore build() {
        if (model == null) {
            throw new UnsupportedOperationException("Model not defined ! Initialize model first by IAMCoreBuilder.withDefaultModel()");
        }
        if (authorizationCodeCache == null) {
            authorizationCodeCache = new AuthorizationCodeCacheImpl(20L, TimeUnit.MINUTES);
        }
        if (tokenCache == null) {
            tokenCache = new TokenCacheImpl(modelCache);
        }
        if (persistenceService == null) {
            persistenceService = new InMemoryPersistenceServiceImpl();
        }
        cacheCleanupScheduler = new CacheCleanupSchedulerImpl(10L, TimeUnit.MINUTES, authorizationCodeCache, tokenCache);
        cacheCleanupScheduler.start();
        clientService = new ClientServiceImpl(modelCache, tokenCache, authorizationCodeCache);
        authenticationService = new AuthenticationServiceImpl(clientService);
        resourceServerService = new ResourceServerServiceImpl(modelCache, tokenCache);
        clientManagementService = new ClientManagementServiceImpl(modelCache);
        organizationManagerService = new OrganizationManagerServiceImpl(modelCache);
        projectManagerService = new ProjectManagerServiceImpl(modelCache);
        userManagerService = new UserManagerServiceImpl(modelCache);
        providerConfigurationService = new ProviderConfigurationServiceImpl(projectManagerService);
        return new IAMCore();
    }

    public static IAMCoreBuilder builder() {
        return new IAMCoreBuilder();
    }

    /**
     * Central interface to get IAM-core essential services.
     */
    public class IAMCore implements AutoCloseable {

        public Model getModel() {
            return model;
        }

        public PersistenceService getPersistenceService() {
            return persistenceService;
        }

        public AuthenticationService getAuthenticationService() {
            return authenticationService;
        }

        public ClientService getClientService() {
            return clientService;
        }

        public ResourceServerService getResourceServerService() {
            return resourceServerService;
        }

        public ClientManagementService getClientManagementService() {
            return clientManagementService;
        }

        public OrganizationManagerService getOrganizationManagerService() {
            return organizationManagerService;
        }

        public ProjectManagerService getProjectManagerService() {
            return projectManagerService;
        }

        public UserManagerService getUserManagerService() {
            return userManagerService;
        }

        public ProviderConfigurationService getProviderConfigurationService() {
            return providerConfigurationService;
        }

        @Override
        public void close() throws Exception {
            cacheCleanupScheduler.close();
        }
    }

}
