package one.microproject.iamservice.examples.performance.tests.dto;

public class ScenarioContext<T,R> {

    private ScenarioRequest<T> scenarioRequest;
    private ScenarioResult<R> scenarioResult;

    public ScenarioContext(ScenarioRequest<T> scenarioRequest) {
        this.scenarioRequest = scenarioRequest;
    }

    public ScenarioRequest<T> getScenarioRequest() {
        return scenarioRequest;
    }

    public ScenarioResult<R> getScenarioResult() {
        return scenarioResult;
    }

    public void setScenarioResult(ScenarioResult<R> scenarioResult) {
        this.scenarioResult = scenarioResult;
    }

}
