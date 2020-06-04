package itx.iamservice.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix="iam-service.system-config")
public class SystemConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SystemConfiguration.class);

    private String id;
    private String name;

    @PostConstruct
    public void init() {
        LOG.info("#CONFIG iam-service.system-config.id: {}", id);
        LOG.info("#CONFIG iam-service.system-config.name: {}", name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
