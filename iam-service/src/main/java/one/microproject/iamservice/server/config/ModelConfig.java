package one.microproject.iamservice.server.config;

import one.microproject.iamservice.core.model.ModelId;
import one.microproject.iamservice.core.model.ModelImpl;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.core.services.caches.ModelCache;
import one.microproject.iamservice.core.services.impl.caches.ModelCacheImpl;
import one.microproject.iamservice.core.services.persistence.DataLoadService;
import one.microproject.iamservice.core.services.persistence.wrappers.ModelWrapper;
import one.microproject.iamservice.persistence.filesystem.FileSystemDataLoadServiceImpl;
import one.microproject.iamservice.persistence.filesystem.FileSystemPersistenceServiceImpl;
import one.microproject.iamservice.persistence.mongo.MongoConfiguration;
import one.microproject.iamservice.persistence.mongo.MongoModelWrapperImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    private String mongoHost;
    private Integer mongoPort;
    private String mongoDatabase;
    private String mongoUsername;
    private String mongoPassword;

    private ModelCache modelCache;

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
    public ModelCache createModelCache() throws Exception {
        if ("file-system".equals(persistence)) {
            try {
                LOG.info("#CONFIG: populating ModelCache from file: {}", path);
                DataLoadService dataLoadService = new FileSystemDataLoadServiceImpl(Path.of(path));
                ModelWrapper modelWrapper = dataLoadService.populateCache();
                modelWrapper.onInit(new FileSystemPersistenceServiceImpl(Path.of(path)), false);
                modelCache = new ModelCacheImpl(modelWrapper);
                LOG.info("#CONFIG: ModelCache loaded from file OK");
                return modelCache;
            } catch (Exception e) {
                LOG.error("#CONFIG: ModelCache loading from filesystem failed. ERROR: {}", e.getMessage());
            }
            try {
                LOG.info("#CONFIG: creating default model");
                ModelWrapper modelWrapper = ModelUtils.createModelWrapper("default-model", new FileSystemPersistenceServiceImpl(Path.of(path)), false);
                modelCache = ModelUtils.createDefaultModelCache(
                        OrganizationId.from(adminOrganization), ProjectId.from(adminProject), defaultAdminPassword, defaultAdminClientSecret, defaultAdminEmail, modelWrapper);
                modelCache.flush();
                LOG.info("#CONFIG: ModelCache with default model initialized OK");
                return modelCache;
            } catch (Exception e) {
                LOG.error("Error: {}", e.getMessage());
                throw e;
            }
        } else if ("mongo-db".equals(persistence)) {
            LOG.info("#CONFIG: creating mongo-db backed model cache");
            LOG.info("#CONFIG: mongoHost=mongodb://{}:******@{}:{}/{}", mongoUsername, mongoHost, mongoPort, mongoDatabase);
            ModelWrapper modelWrapper = new MongoModelWrapperImpl(new MongoConfiguration(mongoHost, mongoPort, mongoDatabase, mongoUsername, mongoPassword));
            if (!modelWrapper.isInitialized()) {
                LOG.info("#CONFIG: initializing mongo-db with default model");
                modelWrapper.setModel(new ModelImpl(ModelId.from("default-model"), ""));
                modelCache = ModelUtils.createDefaultModelCache(
                        OrganizationId.from(adminOrganization), ProjectId.from(adminProject), defaultAdminPassword, defaultAdminClientSecret, defaultAdminEmail, modelWrapper);
            } else {
                LOG.info("#CONFIG: mongo-db model is already initialized !");
                modelCache = new ModelCacheImpl(modelWrapper);
            }
            return modelCache;
        } else {
            LOG.info("#CONFIG: default ModelWrapper created");
            ModelWrapper modelWrapper = ModelUtils.createInMemoryModelWrapper("default-model");
            modelCache = ModelUtils.createDefaultModelCache(
                    OrganizationId.from(adminOrganization), ProjectId.from(adminProject), defaultAdminPassword, defaultAdminClientSecret, defaultAdminEmail, modelWrapper);
            return modelCache;
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            LOG.info("#CONFIG: Flushing model data ...");
            modelCache.flush();
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

    public String getMongoHost() {
        return mongoHost;
    }

    public void setMongoHost(String mongoHost) {
        this.mongoHost = mongoHost;
    }

    public Integer getMongoPort() {
        return mongoPort;
    }

    public void setMongoPort(Integer mongoPort) {
        this.mongoPort = mongoPort;
    }

    public String getMongoDatabase() {
        return mongoDatabase;
    }

    public void setMongoDatabase(String mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public String getMongoUsername() {
        return mongoUsername;
    }

    public void setMongoUsername(String mongoUsername) {
        this.mongoUsername = mongoUsername;
    }

    public String getMongoPassword() {
        return mongoPassword;
    }

    public void setMongoPassword(String mongoPassword) {
        this.mongoPassword = mongoPassword;
    }
}
