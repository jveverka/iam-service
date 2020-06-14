package itx.iamservice.server.config;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.caches.ModelCache;
import itx.iamservice.core.services.persistence.DataLoadService;
import itx.iamservice.core.services.persistence.PersistenceService;
import itx.iamservice.persistence.filesystem.FileSystemDataLoadServiceImpl;
import itx.iamservice.persistence.filesystem.FileSystemPersistenceServiceImpl;
import itx.iamservice.persistence.inmemory.InMemoryPersistenceServiceImpl;
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

    private String adminOrganization;
    private String adminProject;

    private String defaultAdminPassword;
    private String defaultAdminSecret;
    private String persistence;
    private String path;

    private PersistenceService persistenceService;

    @PostConstruct
    private void init() {
        LOG.info("#CONFIG: initializing Bouncy Castle Provider (BCP) ...");
        Security.addProvider(new BouncyCastleProvider());
        LOG.info("#CONFIG: BCP initialized.");
        LOG.info("#CONFIG: default admin password initialized={}", !defaultAdminPassword.isEmpty());
        LOG.info("#CONFIG: default admin client secret initialized={}", !defaultAdminSecret.isEmpty());
        LOG.info("#CONFIG: admin organization/project {}/{}", adminOrganization, adminProject);
    }

    @Bean
    @Scope("singleton")
    public ModelCache getModelCache(@Autowired PersistenceService persistenceService) throws PKIException, IOException {
        try {
            if ("file-system".equals(persistence)) {
                LOG.info("#CONFIG: populating ModelCache from file: {}", path);
                DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(Path.of(path), persistenceService);
                return dataLoadService.populateCache();
            } else {
                LOG.info("#CONFIG: default ModelCache created");
                return ModelUtils.createDefaultModelCache(
                        OrganizationId.from(adminOrganization), ProjectId.from(adminProject), defaultAdminPassword, defaultAdminSecret, persistenceService);
            }
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            LOG.warn("#CONFIG: fallback to default ModelCache");
            return ModelUtils.createDefaultModelCache(OrganizationId.from(adminOrganization), ProjectId.from(adminProject),
                    defaultAdminPassword, defaultAdminSecret, persistenceService);
        }
    }

    @Bean
    @Scope("singleton")
    public PersistenceService getPersistenceService() {
        if ("file-system".equals(persistence)) {
            LOG.info("#CONFIG: getPersistenceService: {} path={}", persistence, path);
            persistenceService = new FileSystemPersistenceServiceImpl(Path.of(path), true);
        } else {
            LOG.info("#CONFIG: getPersistenceService: in-memory");
            persistenceService = new InMemoryPersistenceServiceImpl();
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

    public void setDefaultAdminSecret(String defaultAdminSecret) {
        this.defaultAdminSecret = defaultAdminSecret;
    }

    public void setAdminOrganization(String adminOrganization) {
        this.adminOrganization = adminOrganization;
    }

    public void setAdminProject(String adminProject) {
        this.adminProject = adminProject;
    }

}
