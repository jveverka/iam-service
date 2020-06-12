package itx.iamservice.server.config;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.IAMClientBuilder;
import itx.iamservice.client.impl.IAMServiceProxy;
import itx.iamservice.core.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
public class IAMClientConfig {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientConfig.class);

    @Bean
    @Scope("singleton")
    public IAMClient createIAMClient(@Autowired IAMServiceProxy iamServiceProxy) {
        LOG.info("createIAMClient");
        return IAMClientBuilder.builder()
                .setOrganizationId(ModelUtils.IAM_ADMINS_NAME)
                .setProjectId(ModelUtils.IAM_ADMINS_NAME)
                .withIAMServiceProxy(iamServiceProxy)
                .build();
    }

}
