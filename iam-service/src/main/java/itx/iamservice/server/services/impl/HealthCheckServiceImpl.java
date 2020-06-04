package itx.iamservice.server.services.impl;

import itx.iamservice.server.config.SystemConfiguration;
import itx.iamservice.server.services.HealthCheckService;
import itx.iamservice.server.services.dto.HealthCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckServiceImpl.class);

    private final SystemConfiguration systemConfiguration;

    public HealthCheckServiceImpl(@Autowired SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    @Override
    public HealthCheckResponse getStatus() {
        LOG.info("getStatus");
        return new HealthCheckResponse(systemConfiguration.getId(), "iam-service", "OK", "1.0.0",
                systemConfiguration.getName(), System.currentTimeMillis());
    }

}
