package one.microproject.iamservice.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

public class ObjectMapperConfig {

    @Bean
    public ObjectMapper getMapper() {
        return new ObjectMapper();
    }

}
