package itx.examples.webflux.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemInfo {

    private final String name;
    private final String version;

    @JsonCreator
    public SystemInfo(@JsonProperty("name") String name,
                      @JsonProperty("version") String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

}
