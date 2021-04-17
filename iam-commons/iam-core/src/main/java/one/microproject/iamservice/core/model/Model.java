package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ModelImpl.class, name = "iam-model") }
)
public interface Model {

    ModelId getId();

    String getName();

    String getVersion();

}
