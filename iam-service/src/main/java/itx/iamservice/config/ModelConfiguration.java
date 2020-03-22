package itx.iamservice.config;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.PKIException;
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
public class ModelConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ModelConfiguration.class);

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
    public Model getModel() throws PKIException {
        return ModelUtils.createDefaultModel(password);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
