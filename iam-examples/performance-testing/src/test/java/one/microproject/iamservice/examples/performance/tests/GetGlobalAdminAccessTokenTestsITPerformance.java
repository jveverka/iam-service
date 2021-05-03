package one.microproject.iamservice.examples.performance.tests;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.model.TokenType;
import one.microproject.testmeter.ScenarioRunnerBuilder;
import one.microproject.testmeter.dto.RunnerResult;
import one.microproject.testmeter.dto.ScenarioContext;
import one.microproject.testmeter.dto.ScenarioRequest;
import one.microproject.testmeter.dto.ScenarioResult;
import one.microproject.iamservice.examples.performance.tests.impl.GetGlobalAdminAccessTokensTestScenarioProducer;
import one.microproject.iamservice.examples.performance.tests.impl.GlobalAdminContext;
import one.microproject.testmeter.impl.ScenarioRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Stream;

import static one.microproject.testmeter.ITTestUtils.scenariosPerSecond;
import static one.microproject.testmeter.ITTestUtils.successRatePercent;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GetGlobalAdminAccessTokenTestsITPerformance {

    private static final Logger LOG = LoggerFactory.getLogger(GetGlobalAdminAccessTokenTestsITPerformance.class);

    private static ScenarioRunner<GlobalAdminContext, TokenResponse> warmupRunner;
    private static ScenarioRunner<GlobalAdminContext, TokenResponse> scenarioRunner;
    private static int nThreads = 20;
    private static int repeat = 100;

    @BeforeAll
    static void init() {
        GetGlobalAdminAccessTokensTestScenarioProducer scenarioFactory = new GetGlobalAdminAccessTokensTestScenarioProducer();
        warmupRunner = new ScenarioRunnerBuilder<GlobalAdminContext, TokenResponse>()
                .setIndex(1)
                .setNumberOfThreads(10)
                .setRepeat(1)
                .withScenarioProducer(scenarioFactory)
                .build();
        scenarioRunner = new ScenarioRunnerBuilder<GlobalAdminContext, TokenResponse>()
                .setIndex(2)
                .setNumberOfThreads(nThreads)
                .setRepeat(repeat)
                .withScenarioProducer(scenarioFactory)
                .build();
    }

    public static Stream<Arguments> provideParameters() {
        return scenarioRunner.getParameters();
    }

    @Order(1)
    @Test
    void submitWarmupScenarios() {
        assertDoesNotThrow(() -> warmupRunner.execTests());
    }

    @Order(2)
    @Test
    void evaluateWarmup() {
        Collection<ScenarioContext<GlobalAdminContext, TokenResponse>> results = warmupRunner.getResults();
        assertNotNull(results);
        assertEquals(10, results.size());
        RunnerResult runnerResult = warmupRunner.getRunnerResult();
        assertNotNull(runnerResult);
    }

    @Order(3)
    @Test
    void submitScenarios() {
        assertDoesNotThrow(() -> scenarioRunner.execTests());
    }

    @Order(4)
    @ParameterizedTest
    @MethodSource("provideParameters")
    void parametrizedScenarioTest(ScenarioRequest<GlobalAdminContext> request, ScenarioResult<TokenResponse> result) {
        assertNotNull(request.getId());
        assertTrue(result.getSuccess());
        assertTrue(result.getResult().isPresent());
        assertNotNull(result.getResult().get().getAccessToken());
        assertEquals(TokenType.BEARER.getType(), result.getResult().get().getTokenType());
    }

    @Order(5)
    @Test
    void evaluateTests() {
        Collection<ScenarioContext<GlobalAdminContext, TokenResponse>> results = scenarioRunner.getResults();
        assertNotNull(results);
        assertEquals(nThreads*repeat, results.size());
        RunnerResult runnerResult = scenarioRunner.getRunnerResult();
        assertNotNull(runnerResult);
        long passed = results.stream().filter(c->c.getScenarioResult().getSuccess()).count();
        LOG.info("Scenarios/sec: {}", scenariosPerSecond(results.size(), runnerResult.getDuration()));
        LOG.info("Success/sec  : {}", scenariosPerSecond(passed, runnerResult.getDuration()));
        LOG.info("Success rate : {}%", successRatePercent(results.size(), passed));
    }

}
