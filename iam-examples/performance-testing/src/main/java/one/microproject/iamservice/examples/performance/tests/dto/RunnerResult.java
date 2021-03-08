package one.microproject.iamservice.examples.performance.tests.dto;

public class RunnerResult {

    private final Integer nThreads;
    private final Integer repeat;
    private final Long started;
    private final Long duration;

    public RunnerResult(Integer nThreads, Integer repeat, Long started, Long duration) {
        this.nThreads = nThreads;
        this.repeat = repeat;
        this.started = started;
        this.duration = duration;
    }

    public Integer getnThreads() {
        return nThreads;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public Long getStarted() {
        return started;
    }

    public Long getDuration() {
        return duration;
    }

}
