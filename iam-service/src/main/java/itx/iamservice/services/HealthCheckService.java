package itx.iamservice.services;

import itx.iamservice.services.dto.HealthCheckResponse;

public interface HealthCheckService {

    HealthCheckResponse getStatus();

}
