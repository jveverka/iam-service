package itx.iamservice.examples.methodsecurity.controller;

import itx.iamservice.examples.methodsecurity.dto.SystemInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class SystemController {

    @GetMapping("/info")
    ResponseEntity<SystemInfo> getSystemInfo() {
        return ResponseEntity.ok(new SystemInfo("spring-microservice", "1.0.0"));
    }

}
