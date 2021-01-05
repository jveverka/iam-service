package one.microproject.iamservice.server.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class OpenAPIConfig {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAPIConfig.class);

    @Value("#{servletContext.contextPath}")
    private String servletContextPath;

    @PostConstruct
    public void init() {
        LOG.info("#CONFIG servletContextPath={}", getContextPath());
    }

    /**
     * Create OpenAPI 3.0 configuration.
     * @param buildProperties - Springboot {@link BuildProperties} info. May be null if started from IDE.
     * @return {@link OpenAPI} configuration.
     */
    @Bean
    public OpenAPI customOpenAPI(@Autowired(required = false) BuildProperties buildProperties) {
        return new OpenAPI()
                .addServersItem(new Server().url(getContextPath()))
                .info(new Info()
                        .title("IAM-Service")
                        .version((buildProperties != null) ? buildProperties.getVersion() : "SNAPSHOT")
                        .description("")
                        .license(new License().name("License: MIT")
                                .url("https://github.com/jveverka/iam-service/blob/master/LICENSE")));
    }

    public String getServletContextPath() {
        return servletContextPath;
    }

    public void setServletContextPath(String servletContextPath) {
        this.servletContextPath = servletContextPath;
    }

    private String getContextPath() {
        if (servletContextPath == null) {
            return "/";
        } else if (servletContextPath.isEmpty()) {
            return "/";
        } else {
            return servletContextPath;
        }
    }

}
