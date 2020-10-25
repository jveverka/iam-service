package itx.iamservice.serviceclient;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.SetupOrganizationRequest;
import itx.iamservice.core.services.dto.SetupOrganizationResponse;
import itx.iamservice.core.services.dto.TokenResponse;
import itx.iamservice.serviceclient.impl.AuthenticationException;

import java.io.IOException;
import java.util.Collection;

public interface IAMServiceClient {

    TokenResponse getAccessTokens(OrganizationId organizationId, ProjectId projectId, String userName, String password, ClientId clientId, String clientSecret) throws AuthenticationException;

    TokenResponse getAccessTokensForIAMAdmin(String password, String clientSecret) throws AuthenticationException;

    SetupOrganizationResponse setupOrganization(String accessToken, SetupOrganizationRequest setupOrganizationRequest) throws AuthenticationException;

    void deleteOrganizationRecursively(String accessToken, OrganizationId organizationId) throws AuthenticationException;

    IAMServiceProject getIAMServiceProject(String accessToken, OrganizationId organizationId, ProjectId projectId);

    Collection<OrganizationInfo> getOrganizations() throws IOException;

    OrganizationInfo getOrganization(OrganizationId organizationId) throws IOException;

    String getActuatorInfo() throws IOException;

}
