package one.microproject.iamservice.server.config;

import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.impl.persistence.LoggingPersistenceServiceImpl;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.PersistenceService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.persistence.filesystem.FileSystemDataLoadServiceImpl;
import one.microproject.iamservice.persistence.filesystem.FileSystemPersistenceServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Security;

@Configuration
@ConfigurationProperties(prefix="iam-service.data-model")
public class ModelConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ModelConfig.class);

    private String adminOrganization = ModelUtils.IAM_ADMINS_ORG.getId();
    private String adminProject = ModelUtils.IAM_ADMINS_PROJECT.getId();

    private String defaultAdminPassword;
    private String defaultAdminClientSecret;
    private String defaultAdminEmail;
    private String persistence;
    private String path;

    private PersistenceService persistenceService;

    @PostConstruct
    private void init() {
        LOG.info("#CONFIG: initializing Bouncy Castle Provider (BCP) ...");
        Security.addProvider(new BouncyCastleProvider());
        LOG.info("#CONFIG: BCP initialized.");
        LOG.info("#CONFIG: default admin password initialized={}", !defaultAdminPassword.isEmpty());
        LOG.info("#CONFIG: default admin client secret initialized={}", !defaultAdminClientSecret.isEmpty());
        LOG.info("#CONFIG: default admin email={}", defaultAdminEmail);
        LOG.info("#CONFIG: admin organization/project {}/{}", adminOrganization, adminProject);
        LOG.info("#CONFIG: persistence={}", persistence);
    }

    @Bean
    @Scope("singleton")
    public ModelCache getModelCache() throws PKIException, IOException {
        try {
            if ("file-system".equals(persistence)) {
                LOG.info("#CONFIG: populating ModelCache from file: {}", path);
                DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(Path.of(path));
                ModelWrapper modelWrapper = dataLoadService.populateCache();
                return new ModelCacheImpl(modelWrapper);
            } else {
                LOG.info("#CONFIG: default ModelCache created");
                return ModelUtils.createDefaultModelCache(
                        OrganizationId.from(adminOrganization), ProjectId.from(adminProject), defaultAdminPassword, defaultAdminClientSecret, defaultAdminEmail, new LoggingPersistenceServiceImpl());
            }
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            LOG.warn("#CONFIG: fallback to default ModelCache");
            return ModelUtils.createDefaultModelCache(OrganizationId.from(adminOrganization), ProjectId.from(adminProject),
                    defaultAdminPassword, defaultAdminClientSecret, defaultAdminEmail, new LoggingPersistenceServiceImpl());
        }
    }

    @Bean
    @Scope("singleton")
    public PersistenceService getPersistenceService(@Autowired ModelCache modelCache) {
        if ("file-system".equals(persistence)) {
            LOG.info("#CONFIG: getPersistenceService: {} path={}", persistence, path);
            persistenceService = new FileSystemPersistenceServiceImpl(Path.of(path), true, modelCache.export());
        } else {
            LOG.info("#CONFIG: getPersistenceService: in-memory");
            persistenceService = new LoggingPersistenceServiceImpl();
        }
        return persistenceService;
    }

    @PreDestroy
    public void shutdown() {
        try {
            LOG.info("#CONFIG: Flushing persistence service");
            persistenceService.flush();
        } catch (Exception e) {
            LOG.error("Error: ", e);
        }
    }

    public void setPersistence(String persistence) {
        this.persistence = persistence;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDefaultAdminPassword(String defaultAdminPassword) {
        this.defaultAdminPassword = defaultAdminPassword;
    }

    public void setDefaultAdminClientSecret(String defaultAdminClientSecret) {
        this.defaultAdminClientSecret = defaultAdminClientSecret;
    }

    public void setDefaultAdminEmail(String defaultAdminEmail) {
        this.defaultAdminEmail = defaultAdminEmail;
    }

}
