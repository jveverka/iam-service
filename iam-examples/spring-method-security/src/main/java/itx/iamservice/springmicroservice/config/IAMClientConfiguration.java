package itx.iamservice.springmicroservice.config;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.IAMClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
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
    public IAMClient createIAMClient() throws MalformedURLException {
        LOG.info("createIAMClient");
        return IAMClientBuilder.builder()
                .setOrganizationId(organizationId)
                .setProjectId(projectId)
                .withHttpProxy(new URL(baseUrl), pollingInterval, timeUnit)
                .build();
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
