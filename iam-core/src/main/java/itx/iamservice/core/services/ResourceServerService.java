package itx.iamservice.core.services;

import itx.iamservice.core.model.User;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.core.services.dto.JWToken;
import itx.iamservice.core.services.dto.ProjectInfo;

import java.util.Optional;

/**
 *  Service providing verification of {@link JWToken} instances.
 *  This service is intended to be used mainly by OAuth2 "resource server" roles.
 *  @see <a href="https://tools.ietf.org/html/rfc6749#section-1.1">OAuth2 roles</a>
 */
public interface ResourceServerService {

    /**
     * Verify if JWT token is valid. Time stamps and signature is verified.
     * @param organizationId {@link OrganizationId} unique organization ID.
     * @param projectId {@link ProjectId} unique project ID.
     * @param token {@link JWToken} to verify.
     * @return true if provided {@link JWToken} is valid, false otherwise.
     */
    boolean verify(OrganizationId organizationId, ProjectId projectId, JWToken token);

    /**
     * Get public {@link Project} related information. This information contains X509 certificates of organization and project.
     * @param organizationId {@link OrganizationId} - unique id of the organization.
     * @param projectId {@link ProjectId} - unique id of the project.
     * @return Optional of {@link ProjectInfo} instance if project and organization exists, empty otherwise.
     */
    Optional<ProjectInfo> getProjectInfo(OrganizationId organizationId, ProjectId projectId);

    /**
     * Get public {@link User} related information. This information contains X509 of user.
     * @param organizationId {@link OrganizationId} - unique id of the organization.
     * @param projectId {@link ProjectId} - unique id of the project.
     * @param userId {@link UserId} - unique id of the user.
     * @return Optional of {@link UserInfo} instance if project, organization and user exists, empty otherwise.
     */
    Optional<UserInfo> getUserInfo(OrganizationId organizationId, ProjectId projectId, UserId userId);

}
