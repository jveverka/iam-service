package one.microproject.iamservice.examples.performance.tests.dto;

public class RunnerContext {

    private final int runnerIndex;
    private final int index;

    public RunnerContext(int runnerIndex, int index) {
        this.runnerIndex = runnerIndex;
        this.index = index;
    }

    public int getRunnerIndex() {
        return runnerIndex;
    }

    public int getIndex() {
        return index;
    }

}
