package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "typeId")
public interface Credentials<T extends AuthenticationRequest> {

    UserId getUserId();

    @JsonIgnore
    Class<? extends Credentials> getType();

    boolean verify(T authenticationRequest);

}
