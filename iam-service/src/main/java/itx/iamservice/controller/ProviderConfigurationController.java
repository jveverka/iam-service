package itx.iamservice.controller;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.ProviderConfigurationService;
import itx.iamservice.core.services.dto.ProviderConfigurationRequest;
import itx.iamservice.core.services.dto.ProviderConfigurationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping(path = "/config")
public class ProviderConfigurationController {

    private final ProviderConfigurationService providerConfigurationService;

    public ProviderConfigurationController(@Autowired ProviderConfigurationService providerConfigurationService) {
        this.providerConfigurationService = providerConfigurationService;
    }

    @GetMapping(path = "/{organization-id}/{project-id}/.well-known/openid-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProviderConfigurationResponse> getConfiguration(@PathVariable("organization-id") String organizationId,
                                                                          @PathVariable("project-id") String projectId,
                                                                          HttpServletRequest request) throws MalformedURLException {
        URL url = new URL(request.getRequestURL().toString());
        String baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/services/authentication";
        ProviderConfigurationRequest providerConfigurationRequest = new ProviderConfigurationRequest(baseUrl, OrganizationId.from(organizationId), ProjectId.from(projectId));
        ProviderConfigurationResponse configuration = providerConfigurationService.getConfiguration(providerConfigurationRequest);
        return ResponseEntity.ok(configuration);
    }

}
