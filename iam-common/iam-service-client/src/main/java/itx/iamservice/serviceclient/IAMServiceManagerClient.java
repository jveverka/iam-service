package itx.iamservice.serviceclient;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import itx.iamservice.serviceclient.impl.AuthenticationException;

import java.io.IOException;
import java.util.Collection;

public interface IAMServiceManagerClient {

    SetupOrganizationResponse setupOrganization(String accessToken, SetupOrganizationRequest setupOrganizationRequest) throws AuthenticationException;

    void deleteOrganizationRecursively(String accessToken, OrganizationId organizationId) throws AuthenticationException;

    Collection<OrganizationInfo> getOrganizations() throws IOException;

    OrganizationInfo getOrganization(OrganizationId organizationId) throws IOException;

    String getActuatorInfo() throws IOException;

    IAMServiceProjectManagerClient getIAMServiceProject(String accessToken, OrganizationId organizationId, ProjectId projectId);

    IAMServiceStatusClient getIAMServiceStatusClient(OrganizationId organizationId, ProjectId projectId);

    IAMAuthorizerClient getIAMAuthorizerClient(OrganizationId organizationId, ProjectId projectId);

    IAMAuthorizerClient getIAMAdminAuthorizerClient();

    IAMServiceUserManagerClient getIAMServiceUserManagerClient(String accessToken, OrganizationId organizationId, ProjectId projectId);

}
