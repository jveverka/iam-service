package one.microproject.iamservice.examples.performance.tests.impl;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.examples.performance.tests.ResultCache;
import one.microproject.iamservice.examples.performance.tests.ScenarioFactory;
import one.microproject.iamservice.examples.performance.tests.ScenarioInitException;
import one.microproject.iamservice.examples.performance.tests.TestScenario;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioRequest;

import java.net.MalformedURLException;

import static one.microproject.iamservice.examples.performance.tests.ITTestUtils.getGlobalAdminClientSecret;
import static one.microproject.iamservice.examples.performance.tests.ITTestUtils.getGlobalAdminPassword;
import static one.microproject.iamservice.examples.performance.tests.ITTestUtils.getIAMServiceURL;

public class GetGlobalAdminAccessTokensTestScenarioFactory implements ScenarioFactory<GlobalAdminContext, TokenResponse> {

    @Override
    public TestScenario<GlobalAdminContext, TokenResponse> createScenario(ResultCache<GlobalAdminContext, TokenResponse> cache, int ordinal) throws Exception {
        GlobalAdminContext globalAdminContext = new GlobalAdminContext(getIAMServiceURL(), getGlobalAdminPassword(), getGlobalAdminClientSecret());
        ScenarioRequest<GlobalAdminContext> request = new ScenarioRequest<>(ordinal, globalAdminContext);
        return new GetGlobalAdminAccessTokensTestScenario(cache, request);
    }

}
