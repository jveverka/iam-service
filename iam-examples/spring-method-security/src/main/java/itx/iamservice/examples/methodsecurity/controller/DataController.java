package itx.iamservice.examples.methodsecurity.controller;

import itx.iamservice.examples.methodsecurity.dto.ServerData;
import itx.iamservice.examples.methodsecurity.services.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class DataController {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    private final DataService dataService;

    public DataController(@Autowired DataService dataService) {
        this.dataService = dataService;
    }

    @Secured({"ROLE_iam-admin-service.users.read"})
    @GetMapping("/data")
    public ResponseEntity<ServerData> getData(Authentication authentication) {
        LOG.info("getData authentication={}", authentication.getName());
        return ResponseEntity.ok(dataService.getData());
    }

    @Secured({"ROLE_iam-admin-service.users.create"})
    @PutMapping("/data")
    public ResponseEntity<Void> setData(@RequestBody ServerData serverData,  Authentication authentication) {
        LOG.info("setData authentication={} serverData={}", authentication.getName(), serverData.getData());
        dataService.setData(serverData);
        return ResponseEntity.ok().build();
    }

}
