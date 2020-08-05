package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrganizationImpl.class, name = "organization") })
public interface Organization {

    OrganizationId getId();

    String getName();

    void addProject(ProjectId id);

    Collection<ProjectId> getProjects();

    boolean removeProject(ProjectId id);

    KeyPairSerialized getKeyPairSerialized();

    @JsonIgnore
    PrivateKey getPrivateKey();

    @JsonIgnore
    X509Certificate getCertificate();

    @JsonIgnore
    KeyPairData getKeyPairData();

    Map<String, String> getProperties();

    void setProperty(String key, String value);

    void removeProperty(String key);

}
