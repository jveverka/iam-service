package one.microproject.iamservice.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix="iam-service.base-url-mapping")
public class BaseUrlMapperConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BaseUrlMapperConfig.class);

    private String baseUrl;
    private String mappedUrl;

    @PostConstruct
    public void init() {
        LOG.info("#CONFIG iam-service.base-url-mapping.base-url  : {}", baseUrl);
        LOG.info("#CONFIG iam-service.base-url-mapping.mapped-url: {}", mappedUrl);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getMappedUrl() {
        return mappedUrl;
    }

    public void setMappedUrl(String mappedUrl) {
        this.mappedUrl = mappedUrl;
    }

}
