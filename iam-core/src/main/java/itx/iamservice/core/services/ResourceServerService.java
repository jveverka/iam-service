package itx.iamservice.core.services;

import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.dto.ClientInfo;
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
     * Get public {@link Client} related information. This information contains X509 of client.
     * @param organizationId {@link OrganizationId} - unique id of the organization.
     * @param projectId {@link ProjectId} - unique id of the project.
     * @param clientId {@link ClientId} - unique id of the client.
     * @return Optional of {@link ClientInfo} instance if project, organization and client exists, empty otherwise.
     */
    Optional<ClientInfo> getClientInfo(OrganizationId organizationId, ProjectId projectId, ClientId clientId);

}
