package one.microproject.iamservice.examples.methodsecurity.controller;

import one.microproject.iamservice.client.spring.IAMSecurityFilterConfiguration;
import one.microproject.iamservice.examples.methodsecurity.dto.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services/public")
public class SystemController {

    private static final Logger LOG = LoggerFactory.getLogger(SystemController.class);

    private final IAMSecurityFilterConfiguration iamSecurityFilterConfiguration;

    public SystemController(@Autowired IAMSecurityFilterConfiguration iamSecurityFilterConfiguration) {
        this.iamSecurityFilterConfiguration = iamSecurityFilterConfiguration;
    }

    @GetMapping("/info")
    ResponseEntity<SystemInfo> getSystemInfo() {
        LOG.info("getSystemInfo");
        return ResponseEntity.ok(new SystemInfo("method-security-microservice", "1.0.0"));
    }

    @GetMapping("/update-iam-client-cache")
    ResponseEntity<Void> updateIamClientCache() {
        LOG.info("updateIamClientCache");
        iamSecurityFilterConfiguration.getIamClient().updateKeyCache();
        return ResponseEntity.ok().build();
    }

}
