package one.microproject.iamservice.examples.performance.tests;

import one.microproject.iamservice.examples.performance.tests.dto.RunnerContext;
import one.microproject.iamservice.examples.performance.tests.dto.ScenarioRequest;
import one.microproject.iamservice.examples.performance.tests.impl.ScenarioInitException;
import one.microproject.iamservice.examples.performance.tests.impl.ScenarioRunner;

/**
 * Producer called by {@link ScenarioRunner}.
 * @param <T> - request type, input data into scenario execution.
 * @param <R> - response type, the result of scenario execution.
 */
public interface TestScenarioProducer<T, R> {

    /**
     * Create scenario request using context.
     * @param context - context provided by {@link ScenarioRunner}.
     * @return instance of {@link ScenarioRequest}
     * @throws ScenarioInitException - thrown in case that scenario request create fails.
     */
    ScenarioRequest<T> createRequest(RunnerContext context) throws ScenarioInitException;

    /**
     * Create test scenario based using context.
     * @param context - context provided by {@link ScenarioRunner}.
     * @return instance of {@link TestScenario}
     * @throws ScenarioInitException - thrown in case that scenario create fails.
     */
    TestScenario<T, R> createScenario(RunnerContext context) throws ScenarioInitException;

}
