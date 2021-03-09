package one.microproject.iamservice.examples.performance.tests.impl;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.examples.performance.tests.TestScenario;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;

import java.io.IOException;

public class GetGlobalAdminAccessTokensTestScenario implements TestScenario<GlobalAdminContext, TokenResponse> {

    private final IAMServiceManagerClient iamServiceManagerClient;

    public GetGlobalAdminAccessTokensTestScenario(IAMServiceManagerClient iamServiceManagerClient) {
        this.iamServiceManagerClient = iamServiceManagerClient;
    }

    @Override
    public TokenResponse getResult(GlobalAdminContext request) throws ScenarioExecException {
        try {
            TokenResponseWrapper tokenResponseWrapper = iamServiceManagerClient.getIAMAdminAuthorizerClient()
                    .getAccessTokensOAuth2UsernamePassword("admin", request.getPassword(), ModelUtils.IAM_ADMIN_CLIENT_ID, request.getClientSecret());
            return tokenResponseWrapper.getTokenResponse();
        } catch (IOException e) {
            throw new ScenarioExecException(e);
        }
    }

}
