package itx.iamservice.examples.methodsecurity.config;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.IAMClientBuilder;
import itx.iamservice.client.spring.IAMSecurityFilterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix="iam-client")
public class IAMClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientConfiguration.class);

    private String organizationId;
    private String projectId;
    private String baseUrl;
    private Long pollingInterval;
    private  TimeUnit timeUnit;

    @PostConstruct
    public void init() {
        LOG.info("## organizationId={}", organizationId);
        LOG.info("## projectId={}", projectId);
        LOG.info("## baseUrl={}", baseUrl);
        LOG.info("## pollingInterval={}", pollingInterval);
        LOG.info("## timeUnit={}", timeUnit);
    }

    @Bean
    @Scope("singleton")
    public IAMSecurityFilterConfiguration createIAMSecurityFilterConfiguration() throws MalformedURLException {
        LOG.info("createIAMClient");
        IAMClient iamClient = IAMClientBuilder.builder()
                .setBaseUrl(new URL(baseUrl))
                .setOrganizationId(organizationId)
                .setProjectId(projectId)
                .withHttpProxy(pollingInterval, timeUnit)
                .build();
        Set<String> excludeEndpoints = Set.of("/services/public/**");
        return new IAMSecurityFilterConfiguration(iamClient, excludeEndpoints);
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setPollingInterval(Long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

}
