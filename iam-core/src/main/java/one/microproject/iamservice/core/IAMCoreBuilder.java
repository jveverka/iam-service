package one.microproject.iamservice.core;

import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.services.AuthenticationService;
import one.microproject.iamservice.core.services.ProviderConfigurationService;
import one.microproject.iamservice.core.services.ResourceServerService;
import one.microproject.iamservice.core.services.admin.ClientManagementService;
import one.microproject.iamservice.core.services.admin.OrganizationManagerService;
import one.microproject.iamservice.core.services.admin.ProjectManagerService;
import one.microproject.iamservice.core.services.admin.UserManagerService;
import one.microproject.iamservice.core.services.caches.AuthorizationCodeCache;
import one.microproject.iamservice.core.services.caches.CacheCleanupScheduler;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.caches.TokenCache;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.services.impl.AuthenticationServiceImpl;
import one.microproject.iamservice.core.services.impl.caches.CacheHolder;
import one.microproject.iamservice.core.services.impl.caches.CacheHolderImpl;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.impl.ProviderConfigurationServiceImpl;
import one.microproject.iamservice.core.services.impl.ResourceServerServiceImpl;
import one.microproject.iamservice.core.services.impl.admin.ClientManagementServiceImpl;
import one.microproject.iamservice.core.services.impl.admin.OrganizationManagerServiceImpl;
import one.microproject.iamservice.core.services.impl.admin.ProjectManagerServiceImpl;
import one.microproject.iamservice.core.services.impl.admin.UserManagerServiceImpl;
import one.microproject.iamservice.core.services.impl.caches.AuthorizationCodeCacheImpl;
import one.microproject.iamservice.core.services.impl.caches.CacheCleanupSchedulerImpl;
import one.microproject.iamservice.core.services.impl.caches.TokenCacheImpl;
import one.microproject.iamservice.core.services.impl.persistence.LoggingPersistenceServiceImpl;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapperImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.concurrent.TimeUnit;

import static one.microproject.iamservice.core.model.utils.ModelUtils.DEFAULT_MODEL;

public class IAMCoreBuilder {

    private ModelCache modelCache;
    private ModelWrapper modelWrapper;
    private CacheCleanupScheduler cacheCleanupScheduler;
    private AuthorizationCodeCache authorizationCodeCache;
    private TokenCache tokenCache;
    private AuthenticationService authenticationService;
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

    public IAMCoreBuilder withModelWrapper(ModelWrapper modelWrapper) {
        this.modelWrapper = modelWrapper;
        this.modelCache = new ModelCacheImpl(modelWrapper);
        return this;
    }

    public IAMCoreBuilder withDefaultModel(String iamAdminPassword, String iamClientSecret, String iamAdminEmail) throws PKIException {
        if (modelWrapper == null) {
            modelWrapper = new ModelWrapperImpl(DEFAULT_MODEL, new LoggingPersistenceServiceImpl(), false);
        }
        this.modelCache = ModelUtils.createDefaultModelCache(iamAdminPassword, iamClientSecret, iamAdminEmail, modelWrapper);
        return this;
    }

    public IAMCoreBuilder withAuthorizationCodeCache(AuthorizationCodeCache authorizationCodeCache) {
        this.authorizationCodeCache = authorizationCodeCache;
        return this;
    }

    public IAMCoreBuilder withAuthorizationCodeCache(Long maxDuration, TimeUnit timeUnit, CacheHolder<AuthorizationCodeContext> cache) {
        this.authorizationCodeCache = new AuthorizationCodeCacheImpl(maxDuration, timeUnit, cache);
        return this;
    }

    public IAMCoreBuilder withDefaultAuthorizationCodeCache(Long maxDuration, TimeUnit timeUnit) {
        this.authorizationCodeCache = new AuthorizationCodeCacheImpl(maxDuration, timeUnit, new CacheHolderImpl<>());
        return this;
    }

    public IAMCoreBuilder withTokenCache(TokenCache tokenCache) {
        this.tokenCache = tokenCache;
        return this;
    }

    public IAMCoreBuilder withTokenCache(CacheHolder<JWToken> cache) {
        this.tokenCache = new TokenCacheImpl(modelCache, cache);
        return this;
    }

    public IAMCoreBuilder withDefaultTokenCache() {
        this.tokenCache = new TokenCacheImpl(modelCache, new CacheHolderImpl<>());
        return this;
    }

    public IAMCore build() {
        if (authorizationCodeCache == null) {
            authorizationCodeCache = new AuthorizationCodeCacheImpl(20L, TimeUnit.MINUTES, new CacheHolderImpl<>());
        }
        if (tokenCache == null) {
            tokenCache = new TokenCacheImpl(modelCache, new CacheHolderImpl<>());
        }
        cacheCleanupScheduler = new CacheCleanupSchedulerImpl(10L, TimeUnit.MINUTES, authorizationCodeCache, tokenCache);
        cacheCleanupScheduler.start();
        authenticationService = new AuthenticationServiceImpl(modelCache, tokenCache, authorizationCodeCache);
        resourceServerService = new ResourceServerServiceImpl(modelCache, tokenCache);
        clientManagementService = new ClientManagementServiceImpl(modelCache);
        organizationManagerService = new OrganizationManagerServiceImpl(modelCache);
        projectManagerService = new ProjectManagerServiceImpl(modelCache);
        userManagerService = new UserManagerServiceImpl(modelCache);
        providerConfigurationService = new ProviderConfigurationServiceImpl(organizationManagerService, projectManagerService);
        return new IAMCore();
    }

    public static IAMCoreBuilder builder() {
        return new IAMCoreBuilder();
    }

    /**
     * Central interface to get IAM-core essential services.
     */
    public class IAMCore implements AutoCloseable {

        public ModelCache getModelCache() {
            return modelCache;
        }

        public AuthenticationService getAuthenticationService() {
            return authenticationService;
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
