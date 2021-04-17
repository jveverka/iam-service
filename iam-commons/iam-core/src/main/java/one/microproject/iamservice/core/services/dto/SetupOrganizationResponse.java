package one.microproject.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.model.UserProperties;

import java.util.Set;

public class SetupOrganizationResponse extends SetupOrganizationRequest {

    private final String adminRoleId;
    private final Set<String> adminPermissions;

    public SetupOrganizationResponse(SetupOrganizationRequest request, String adminRoleId, Set<String> adminPermissions) {
        super(request.getOrganizationId(),
                request.getOrganizationName(),
                request.getProjectId(),
                request.getProjectName(),
                request.getAdminClientId(),
                request.getAdminClientSecret(),
                request.getAdminUserId(),
                request.getAdminUserPassword(),
                request.getAdminUserEmail(),
                request.getProjectAudience(),
                request.getRedirectURL(),
                request.getAdminUserProperties());
        this.adminRoleId = adminRoleId;
        this.adminPermissions = adminPermissions;
    }

    @JsonCreator
    public SetupOrganizationResponse(@JsonProperty("organizationId") String organizationId,
                                     @JsonProperty("organizationName") String organizationName,
                                     @JsonProperty("projectId") String projectId,
                                     @JsonProperty("projectName") String projectName,
                                     @JsonProperty("adminClientId") String adminClientId,
                                     @JsonProperty("adminClientSecret") String adminClientSecret,
                                     @JsonProperty("adminUserId") String adminUserId,
                                     @JsonProperty("adminUserPassword") String adminUserPassword,
                                     @JsonProperty("projectAudience") Set<String> projectAudience,
                                     @JsonProperty("adminRoleId") String adminRoleId,
                                     @JsonProperty("adminUserEmail") String adminUserEmail,
                                     @JsonProperty("adminPermissions") Set<String> adminPermissions,
                                     @JsonProperty("redirectURL") String redirectURL,
                                     @JsonProperty("adminUserProperties") UserProperties adminUserProperties) {
        super(organizationId, organizationName, projectId, projectName, adminClientId, adminClientSecret, adminUserId, adminUserPassword, adminUserEmail, projectAudience, redirectURL, adminUserProperties);
        this.adminRoleId = adminRoleId;
        this.adminPermissions = adminPermissions;
    }

    public String getAdminRoleId() {
        return adminRoleId;
    }

    public Set<String> getAdminPermissions() {
        return adminPermissions;
    }

}
