package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

public class ProviderConfigurationRequest {

    private final String baseURL;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public ProviderConfigurationRequest(String baseURL, OrganizationId organizationId, ProjectId projectId) {
        this.baseURL = baseURL;
        this.organizationId = organizationId;
        this.projectId = projectId;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

}
