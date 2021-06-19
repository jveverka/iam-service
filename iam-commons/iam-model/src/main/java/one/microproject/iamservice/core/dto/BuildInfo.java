package one.microproject.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildInfo {

    private final String id;
    private final String group;
    private final String artefact;
    private final String name;
    private final String version;
    private final Long timestamp;
    private final Long uptime;
    private final String timezone;

    @JsonCreator
    public BuildInfo(@JsonProperty("id") String id,
                     @JsonProperty("group") String group,
                     @JsonProperty("artefact") String artefact,
                     @JsonProperty("name") String name,
                     @JsonProperty("version") String version,
                     @JsonProperty("timestamp") Long timestamp,
                     @JsonProperty("uptime") Long uptime,
                     @JsonProperty("timezone") String timezone) {
        this.id = id;
        this.group = group;
        this.artefact = artefact;
        this.name = name;
        this.version = version;
        this.timestamp = timestamp;
        this.uptime = uptime;
        this.timezone = timezone;
    }

    public BuildInfo(String id, Long timestamp, Long uptime, String timezone) {
        this.id = id;
        this.group = null;
        this.artefact = null;
        this.name = null;
        this.version = null;
        this.timestamp = timestamp;
        this.uptime = uptime;
        this.timezone = timezone;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public String getArtefact() {
        return artefact;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getUptime() {
        return uptime;
    }

    public String getTimezone() {
        return timezone;
    }
}
