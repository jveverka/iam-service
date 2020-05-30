package itx.iamservice.iamresourceserver.controller;

import itx.iamservice.iamresourceserver.dto.ServerData;
import itx.iamservice.iamresourceserver.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class DataController {

    private final DataService dataService;

    public DataController(@Autowired DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/data")
    public ResponseEntity<ServerData> getData() {
        return ResponseEntity.ok(dataService.getData());
    }

    @PutMapping("/data")
    public ResponseEntity<Void> setData(@RequestBody ServerData serverData) {
        dataService.setData(serverData);
        return ResponseEntity.ok().build();
    }
}
