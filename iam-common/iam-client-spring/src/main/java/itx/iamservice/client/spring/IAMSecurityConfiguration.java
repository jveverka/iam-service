package itx.iamservice.client.spring;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.IAMClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties("iam-service.client")
public class IAMSecurityConfiguration {

    private String baseUrl;
    private String organizationId;
    private String projectId;
    private Long pollingInterval;
    private TimeUnit timeUnit;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(Long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Bean
    @Scope("singleton")
    public IAMClient getIAMClient() throws MalformedURLException {
        return IAMClientBuilder.builder()
                .setOrganizationId(organizationId)
                .setProjectId(projectId)
                .withHttpProxy(new URL(baseUrl), pollingInterval, timeUnit)
                .build();
    }

}
