package itx.iamservice.server.services;

import itx.iamservice.core.dto.HealthCheckResponse;

@Deprecated
public interface HealthCheckService {

    HealthCheckResponse getStatus();

}
