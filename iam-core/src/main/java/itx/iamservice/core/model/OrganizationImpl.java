package itx.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.model.utils.TokenUtils;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OrganizationImpl implements Organization {

    private final OrganizationId id;
    private final String name;
    private final Map<ProjectId, Project> projects;
    private final KeyPairData keyPairData;
    private final KeyPairSerialized keyPairSerialized;

    public OrganizationImpl(OrganizationId id, String name) throws PKIException {
        this.id = id;
        this.name = name;
        this.projects = new ConcurrentHashMap<>();
        this.keyPairData = TokenUtils.createSelfSignedKeyPairData(id.getId(), 365L, TimeUnit.DAYS);
        this.keyPairSerialized = ModelUtils.serializeKeyPair(keyPairData);
    }

    @JsonCreator
    public OrganizationImpl(@JsonProperty("id") OrganizationId id,
                            @JsonProperty("name") String name,
                            @JsonProperty("projects") Collection<Project> projects,
                            @JsonProperty("keyPairSerialized") KeyPairSerialized keyPairSerialized) throws PKIException {
        this.id = id;
        this.name = name;
        this.projects = new ConcurrentHashMap<>();
        projects.forEach(project -> this.projects.put(project.getId(), project));
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
        return projects.values().stream()
                .filter(project -> project.getOrganizationId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public void add(Project project) {
        projects.put(project.getId(), project);
    }

    @Override
    @JsonIgnore
    public boolean remove(ProjectId projectId) {
        return projects.remove(projectId) != null;
    }

    @Override
    @JsonIgnore
    public Optional<Project> getProject(ProjectId projectId) {
        return Optional.ofNullable(projects.get(projectId));
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
