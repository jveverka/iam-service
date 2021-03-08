package one.microproject.iamservice.examples.performance.tests;

public interface ScenarioFactory<T,R> {

    TestScenario<T,R> createTestScenario(ResultCache<T, R> cache, int ordinal) throws ScenarioInitException;

}
