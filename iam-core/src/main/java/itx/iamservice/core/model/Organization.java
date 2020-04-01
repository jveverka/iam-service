package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
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

    void add(Project project);

    Collection<Project> getProjects();

    boolean remove(ProjectId projectId);

    Optional<Project> getProject(ProjectId projectId);

    PrivateKey getPrivateKey();

    X509Certificate getCertificate();

    KeyPairSerialized getKeyPairSerialized();

    KeyPairData getKeyPairData();

}
