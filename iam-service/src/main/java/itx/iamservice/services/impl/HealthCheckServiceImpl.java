package itx.iamservice.services.impl;

import itx.iamservice.services.HealthCheckService;
import itx.iamservice.services.dto.HealthCheckResponse;

public class HealthCheckServiceImpl implements HealthCheckService {

    @Override
    public HealthCheckResponse getStatus() {
        return new HealthCheckResponse("OK", "1.0.0", System.currentTimeMillis());
    }

}
