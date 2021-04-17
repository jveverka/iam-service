package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import one.microproject.iamservice.core.model.extensions.authentication.up.UPCredentials;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "typeId"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UPCredentials.class, name = "iam-up-credentials") }
)
public interface Credentials<T extends AuthenticationRequest> {

    UserId getUserId();

    @JsonIgnore
    Class<? extends Credentials> getType();

    boolean verify(T authenticationRequest);

}
