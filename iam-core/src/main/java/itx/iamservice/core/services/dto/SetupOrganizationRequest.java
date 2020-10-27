package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class SetupOrganizationRequest {

    private final String organizationId;
    private final String organizationName;
    private final String adminProjectId;
    private final String adminProjectName;
    private final String adminClientId;
    private final String adminClientSecret;
    private final String adminUserId;
    private final String adminUserPassword;
    private final String adminUserEmail;
    private final Set<String> projectAudience;

    @JsonCreator
    public SetupOrganizationRequest(@JsonProperty("organizationId") String organizationId,
                                    @JsonProperty("organizationName") String organizationName,
                                    @JsonProperty("adminProjectId") String adminProjectId,
                                    @JsonProperty("adminProjectName") String adminProjectName,
                                    @JsonProperty("adminClientId") String adminClientId,
                                    @JsonProperty("adminClientSecret") String adminClientSecret,
                                    @JsonProperty("adminUserId") String adminUserId,
                                    @JsonProperty("adminUserPassword") String adminUserPassword,
                                    @JsonProperty("adminUserEmail") String adminUserEmail,
                                    @JsonProperty("projectAudience") Set<String> projectAudience) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.adminProjectId = adminProjectId;
        this.adminProjectName = adminProjectName;
        this.adminClientId = adminClientId;
        this.adminClientSecret = adminClientSecret;
        this.adminUserId = adminUserId;
        this.adminUserPassword = adminUserPassword;
        this.adminUserEmail = adminUserEmail;
        this.projectAudience = projectAudience;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getAdminProjectId() {
        return adminProjectId;
    }

    public String getAdminProjectName() {
        return adminProjectName;
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
}
