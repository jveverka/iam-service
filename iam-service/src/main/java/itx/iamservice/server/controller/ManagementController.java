package itx.iamservice.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;

@RestController
@RequestMapping(path = "/services/management")
public class ManagementController {

    private final ServletContext servletContext;

    public ManagementController(@Autowired ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    //TODO: implement self-service REST APIs

}
