package one.microproject.iamservice.examples.performance.tests.impl;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.testmeter.TestScenario;
import one.microproject.testmeter.TestScenarioProducer;
import one.microproject.testmeter.dto.RunnerContext;
import one.microproject.testmeter.dto.ScenarioRequest;
import one.microproject.iamservice.serviceclient.IAMServiceClientBuilder;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.testmeter.impl.ScenarioInitException;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import static one.microproject.testmeter.ITTestUtils.getGlobalAdminClientSecret;
import static one.microproject.testmeter.ITTestUtils.getGlobalAdminPassword;
import static one.microproject.testmeter.ITTestUtils.getIAMServiceURL;

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
