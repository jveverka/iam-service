package itx.iamservice.services.dto;

public class HealthCheckResponse {

    private final String status;
    private final String version;
    private final Long timestamp;

    public HealthCheckResponse(String status, String version, Long timestamp) {
        this.status = status;
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    public Long getTimestamp() {
        return timestamp;
    }

}
