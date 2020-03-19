package itx.iamservice.services;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.services.dto.TokenRequest;
import itx.iamservice.services.dto.TokenResponse;

import java.util.Optional;

public interface AuthenticationService {

    Optional<TokenResponse> getTokens(OrganizationId organizationId, ProjectId projectId, TokenRequest tokenRequest);

}