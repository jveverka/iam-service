package itx.iamservice.config;

import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.PKIException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.security.Security;

@Configuration
public class ModelConfiguration {

    @PostConstruct
    private void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Bean
    @Scope("singleton")
    public Model getModel() throws PKIException {
        return ModelUtils.createDefaultModel("secret");
    }

}
