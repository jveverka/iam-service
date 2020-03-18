package itx.iamservice.core.services.dto;

import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;

import java.security.cert.X509Certificate;
import java.util.Set;

public class UserInfo {

    private final UserId id;
    private final ProjectId projectId;
    private final OrganizationId organizationId;
    private final String name;
    private final X509Certificate userCertificate;
    private final Set<RoleId> roles;

    public UserInfo(UserId id, ProjectId projectId, OrganizationId organizationId, String name,
                    X509Certificate userCertificate, Set<RoleId> roles) {
        this.id = id;
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.name = name;
        this.userCertificate = userCertificate;
        this.roles = roles;
    }

    public UserId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public X509Certificate getUserCertificate() {
        return userCertificate;
    }

    public Set<RoleId> getRoles() {
        return roles;
    }

}
