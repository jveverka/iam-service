package one.microproject.iamservice.examples.performance.tests.impl;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.examples.performance.tests.ResultCache;
import one.microproject.iamservice.examples.performance.tests.ScenarioExecException;
import one.microproject.iamservice.examples.performance.tests.TestScenario;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioRequest;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GetGlobalAdminAccessTokensTestScenario extends TestScenario<GlobalAdminContext, TokenResponse> {

    private final IAMServiceManagerClient iamServiceManagerClient;
    private final ScenarioRequest<GlobalAdminContext> request;

    public GetGlobalAdminAccessTokensTestScenario(ResultCache<GlobalAdminContext, TokenResponse> resultCache, ScenarioRequest<GlobalAdminContext> request) {
        super(resultCache, request);
        this.request = request;
        this.iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(request.getRequest().getBaseUrl())
                .withConnectionTimeout(10L, TimeUnit.SECONDS)
                .build();
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
