package itx.iamservice.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CreateProject {

    private final String id;
    private final String name;
    private final Set<String> audience;

    @JsonCreator
    public CreateProject(@JsonProperty("id") String id,
                         @JsonProperty("name") String name,
                         @JsonProperty("audience") Collection<String> audience) {
        this.id = id;
        this.name = name;
        this.audience = new HashSet<>();
        audience.forEach(a->this.audience.add(a));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAudience() {
        return audience;
    }

}
