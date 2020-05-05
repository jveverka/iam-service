package itx.iamservice.config;

import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.caches.ModelCache;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.security.Security;

@Configuration
@ConfigurationProperties(prefix="iam-service.admin-credentials")
public class ModelConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ModelConfig.class);

    private String password;

    @PostConstruct
    private void init() {
        LOG.info("#CONFIG: initializing Bouncy Castle Provider ...");
        Security.addProvider(new BouncyCastleProvider());
        LOG.info("#CONFIG: BCP initialized.");
        LOG.info("#CONFIG: admin password initialized={}", !password.isEmpty());
    }

    @Bean
    @Scope("singleton")
    public ModelCache getModelCache() throws PKIException {
        return ModelUtils.createDefaultModelCache(password);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
