package one.microproject.iamservice.serviceclient;

import one.microproject.iamservice.core.dto.BuildInfo;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.dto.ClientInfo;
import one.microproject.iamservice.core.services.dto.OrganizationInfo;
import one.microproject.iamservice.core.services.dto.ProjectInfo;
import one.microproject.iamservice.core.services.dto.SetupOrganizationRequest;
import one.microproject.iamservice.core.services.dto.SetupOrganizationResponse;
import one.microproject.iamservice.core.services.dto.UserInfo;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;

import java.io.IOException;
import java.util.Collection;

public interface IAMServiceManagerClient {

    SetupOrganizationResponse setupOrganization(String accessToken, SetupOrganizationRequest setupOrganizationRequest) throws AuthenticationException;

    void deleteOrganizationRecursively(String accessToken, OrganizationId organizationId) throws AuthenticationException;

    Collection<OrganizationInfo> getOrganizations() throws IOException;

    ProjectInfo getProject(OrganizationId organizationId, ProjectId projectId) throws IOException;

    ClientInfo getClient(OrganizationId organizationId, ProjectId projectId, ClientId clientId) throws IOException;

    UserInfo getUser(OrganizationId organizationId, ProjectId projectId, UserId userId) throws IOException;

    OrganizationInfo getOrganization(OrganizationId organizationId) throws IOException;

    String getActuatorInfo() throws IOException;

    IAMServiceProjectManagerClient getIAMServiceProject(String accessToken, OrganizationId organizationId, ProjectId projectId);

    IAMServiceStatusClient getIAMServiceStatusClient(OrganizationId organizationId, ProjectId projectId);

    IAMAuthorizerClient getIAMAuthorizerClient(OrganizationId organizationId, ProjectId projectId);

    IAMAuthorizerClient getIAMAdminAuthorizerClient();

    IAMServiceUserManagerClient getIAMServiceUserManagerClient(String accessToken, OrganizationId organizationId, ProjectId projectId);

    boolean isServerAlive() throws IOException;

    BuildInfo getBuildInfo() throws IOException;

}
