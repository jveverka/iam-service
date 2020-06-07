package itx.iamservice.server.services;

import itx.iamservice.core.dto.HealthCheckResponse;

public interface HealthCheckService {

    HealthCheckResponse getStatus();

}
