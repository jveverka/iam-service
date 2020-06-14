package itx.iamservice.server.config;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.IAMClientBuilder;
import itx.iamservice.client.impl.IAMServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
@ConfigurationProperties(prefix="iam-service.data-model")
public class IAMClientConfig {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientConfig.class);

    private String adminOrganization;
    private String adminProject;

    @Bean
    @Scope("singleton")
    public IAMClient createIAMClient(@Autowired IAMServiceProxy iamServiceProxy) {
        LOG.info("createIAMClient {}/{}", adminOrganization, adminProject);
        return IAMClientBuilder.builder()
                .setOrganizationId(adminOrganization)
                .setProjectId(adminProject)
                .withIAMServiceProxy(iamServiceProxy)
                .build();
    }

    public void setAdminOrganization(String adminOrganization) {
        this.adminOrganization = adminOrganization;
    }

    public void setAdminProject(String adminProject) {
        this.adminProject = adminProject;
    }

}
