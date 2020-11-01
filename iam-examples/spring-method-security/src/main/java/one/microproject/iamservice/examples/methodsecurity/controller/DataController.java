package one.microproject.iamservice.examples.methodsecurity.controller;

import one.microproject.iamservice.examples.methodsecurity.dto.ServerData;
import one.microproject.iamservice.examples.methodsecurity.services.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services/secure")
public class DataController {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    private final DataService dataService;

    public DataController(@Autowired DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/data")
    @PreAuthorize("hasAuthority('spring-method-security.secure-data.read')")
    public ResponseEntity<ServerData> getData(Authentication authentication) {
        LOG.info("getData");
        logAuthentication(authentication);
        return ResponseEntity.ok(dataService.getData());
    }

    @PostMapping("/data")
    @PreAuthorize("hasAuthority('spring-method-security.secure-data.read') and hasAuthority('spring-method-security.secure-data.write')")
    public ResponseEntity<ServerData> setData(@RequestBody ServerData serverData, Authentication authentication) {
        LOG.info("setData serverData={}", serverData.getData());
        logAuthentication(authentication);
        return ResponseEntity.ok(dataService.setData(serverData));
    }

    private void logAuthentication(Authentication authentication) {
        LOG.info("Authentication: name={}", authentication.getName());
        authentication.getAuthorities().forEach(a-> LOG.info("  GA: {}", a.getAuthority()));
    }

}
