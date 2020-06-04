package itx.iamservice.server.services;

import itx.iamservice.server.services.dto.HealthCheckResponse;

public interface HealthCheckService {

    HealthCheckResponse getStatus();

}
