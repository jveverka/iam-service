package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collection;
import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface Model {

    ModelId getId();

    String getName();

}
