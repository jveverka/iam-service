package itx.iamservice.controller;

import itx.iamservice.services.HealthCheckService;
import itx.iamservice.services.dto.HealthCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/services/health")
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    @Autowired
    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping(path = "/status", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<HealthCheckResponse> getStatus() {
        HealthCheckResponse healthCheckResponse = healthCheckService.getStatus();
        return ResponseEntity.ok(healthCheckResponse);
    }

}
