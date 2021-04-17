package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.UserProperties;

import java.util.Set;

public class SetupOrganizationRequest {

    private final String organizationId;
    private final String organizationName;
    private final String projectId;
    private final String projectName;
    private final String adminClientId;
    private final String adminClientSecret;
    private final String adminUserId;
    private final String adminUserPassword;
    private final String adminUserEmail;
    private final Set<String> projectAudience;
    private final String redirectURL;
    private final UserProperties adminUserProperties;

    @JsonCreator
    public SetupOrganizationRequest(@JsonProperty("organizationId") String organizationId,
                                    @JsonProperty("organizationName") String organizationName,
                                    @JsonProperty("projectId") String projectId,
                                    @JsonProperty("projectName") String projectName,
                                    @JsonProperty("adminClientId") String adminClientId,
                                    @JsonProperty("adminClientSecret") String adminClientSecret,
                                    @JsonProperty("adminUserId") String adminUserId,
                                    @JsonProperty("adminUserPassword") String adminUserPassword,
                                    @JsonProperty("adminUserEmail") String adminUserEmail,
                                    @JsonProperty("projectAudience") Set<String> projectAudience,
                                    @JsonProperty("redirectURL") String redirectURL,
                                    @JsonProperty("adminUserProperties") UserProperties adminUserProperties) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.adminClientId = adminClientId;
        this.adminClientSecret = adminClientSecret;
        this.adminUserId = adminUserId;
        this.adminUserPassword = adminUserPassword;
        this.adminUserEmail = adminUserEmail;
        this.projectAudience = projectAudience;
        this.redirectURL = redirectURL;
        this.adminUserProperties = adminUserProperties;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getAdminClientId() {
        return adminClientId;
    }

    public String getAdminClientSecret() {
        return adminClientSecret;
    }

    public String getAdminUserId() {
        return adminUserId;
    }

    public String getAdminUserPassword() {
        return adminUserPassword;
    }

    public String getAdminUserEmail() {
        return adminUserEmail;
    }

    public Set<String> getProjectAudience() {
        return projectAudience;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public UserProperties getAdminUserProperties() {
        return adminUserProperties;
    }

}
