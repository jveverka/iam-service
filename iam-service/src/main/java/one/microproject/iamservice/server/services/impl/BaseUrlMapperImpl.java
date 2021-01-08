package one.microproject.iamservice.server.services.impl;

import one.microproject.iamservice.server.config.BaseUrlMapperConfig;
import one.microproject.iamservice.server.services.BaseUrlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseUrlMapperImpl implements BaseUrlMapper {

    private final BaseUrlMapperConfig baseUrlMapperConfig;

    public BaseUrlMapperImpl(@Autowired BaseUrlMapperConfig baseUrlMapperConfig) {
        this.baseUrlMapperConfig = baseUrlMapperConfig;
    }

    @Override
    public String mapIfEquals(String baseUrl) {
        if (baseUrl.equals(baseUrlMapperConfig.getBaseUrl())) {
            return baseUrlMapperConfig.getMappedUrl();
        } else {
            return baseUrl;
        }
    }
}
