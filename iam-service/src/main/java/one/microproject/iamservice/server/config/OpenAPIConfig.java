package one.microproject.iamservice.server.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    /**
     * Create OpenAPI 3.0 configuration.
     * @param buildProperties - Springboot {@link BuildProperties} info. May be null if started from IDE.
     * @return {@link OpenAPI} configuration.
     */
    @Bean
    public OpenAPI customOpenAPI(@Autowired(required = false) BuildProperties buildProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title("IAM-Service")
                        .version((buildProperties != null) ? buildProperties.getVersion() : "SNAPSHOT")
                        .description("")
                        .license(new License().name("License: MIT")
                                .url("https://github.com/jveverka/iam-service/blob/master/LICENSE")));
    }

}
