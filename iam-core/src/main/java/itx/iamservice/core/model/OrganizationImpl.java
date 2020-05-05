package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.utils.TokenUtils;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class OrganizationImpl implements Organization {

    private final OrganizationId id;
    private final String name;
    private final Set<ProjectId> projects;
    private final KeyPairData keyPairData;
    private final KeyPairSerialized keyPairSerialized;

    public OrganizationImpl(OrganizationId id, String name) throws PKIException {
        this.id = id;
        this.name = name;
        this.projects = new HashSet<>();
        this.keyPairData = TokenUtils.createSelfSignedKeyPairData(id.getId(), 365L, TimeUnit.DAYS);
        this.keyPairSerialized = ModelUtils.serializeKeyPair(keyPairData);
    }

    @JsonCreator
    public OrganizationImpl(@JsonProperty("id") OrganizationId id,
                            @JsonProperty("name") String name,
                            @JsonProperty("projects") Collection<ProjectId> projects,
                            @JsonProperty("keyPairSerialized") KeyPairSerialized keyPairSerialized) throws PKIException {
        this.id = id;
        this.name = name;
        this.projects = new HashSet<>();
        projects.forEach(project -> this.projects.add(project));
        this.keyPairData = ModelUtils.deserializeKeyPair(keyPairSerialized);
        this.keyPairSerialized = keyPairSerialized;
    }

    @Override
    public OrganizationId getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<Project> getProjects() {
        return ModelProvider.getModel().getProjects(id);
    }

    @Override
    @JsonIgnore
    @Deprecated
    public void add(Project project) {
        projects.add(project.getId());
        ModelProvider.getModel().add(id, project);
    }

    @Override
    @JsonIgnore
    @Deprecated
    public boolean remove(ProjectId projectId) {
        ModelProvider.getModel().remove(id, projectId);
        return projects.remove(projectId);
    }

    @Override
    @JsonIgnore
    @Deprecated
    public Optional<Project> getProject(ProjectId projectId) {
        return ModelProvider.getModel().getProject(id, projectId);
    }

    @Override
    @JsonIgnore
    public PrivateKey getPrivateKey() {
        return keyPairData.getPrivateKey();
    }

    @Override
    @JsonIgnore
    public X509Certificate getCertificate() {
        return keyPairData.getX509Certificate();
    }

    @Override
    public KeyPairSerialized getKeyPairSerialized() {
        return keyPairSerialized;
    }

    @Override
    @JsonIgnore
    public KeyPairData getKeyPairData() {
        return keyPairData;
    }

}
