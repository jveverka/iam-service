package one.microproject.iamservice.examples.performance.tests.impl;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.examples.performance.tests.TestScenario;
import one.microproject.iamservice.examples.performance.tests.TestScenarioProducer;
import one.microproject.iamservice.examples.performance.tests.dto.RunnerContext;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioRequest;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import static one.microproject.iamservice.examples.performance.tests.ITTestUtils.getGlobalAdminClientSecret;
import static one.microproject.iamservice.examples.performance.tests.ITTestUtils.getGlobalAdminPassword;
import static one.microproject.iamservice.examples.performance.tests.ITTestUtils.getIAMServiceURL;

public class GetGlobalAdminAccessTokensTestScenarioProducer implements TestScenarioProducer<GlobalAdminContext, TokenResponse> {

    @Override
    public ScenarioRequest<GlobalAdminContext> createRequest(RunnerContext context) {
        GlobalAdminContext globalAdminContext = new GlobalAdminContext(getGlobalAdminPassword(), getGlobalAdminClientSecret());
        return new ScenarioRequest<>(context.getIndex(), globalAdminContext);
    }

    @Override
    public TestScenario<GlobalAdminContext, TokenResponse> createScenario(RunnerContext context) throws ScenarioInitException {
        try {
            IAMServiceManagerClient iamServiceManagerClient = IAMServiceClientBuilder.builder()
                    .withBaseUrl(getIAMServiceURL())
                    .withConnectionTimeout(10L, TimeUnit.SECONDS)
                    .build();
            return new GetGlobalAdminAccessTokensTestScenario(iamServiceManagerClient);
        } catch (MalformedURLException e) {
            throw new ScenarioInitException(e);
        }
    }

}
