package one.microproject.iamservice.examples.resourceserver.controller;

import one.microproject.iamservice.examples.resourceserver.dto.SystemInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services/public")
public class SystemController {

    @GetMapping("/info")
    ResponseEntity<SystemInfo> getSystemInfo() {
        return ResponseEntity.ok(new SystemInfo("resource-server-microservice", "1.0.0"));
    }

}
