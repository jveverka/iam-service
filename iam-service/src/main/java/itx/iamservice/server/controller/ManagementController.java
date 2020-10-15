package itx.iamservice.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;

@RestController
@RequestMapping(path = "/services/management")
@Tag(name = "Management", description = "APIs providing self-service user management.")
public class ManagementController {

    private final ServletContext servletContext;

    public ManagementController(@Autowired ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    //TODO: implement self-service REST APIs

}
