package one.microproject.iamservice.examples.methodsecurity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IAM-Service: method-security example")
                        .description("")
                        .license(new License().name("License: MIT")
                                .url("https://github.com/jveverka/iam-service/blob/master/LICENSE")));
    }

}
