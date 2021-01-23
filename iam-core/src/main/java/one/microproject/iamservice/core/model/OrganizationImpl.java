package one.microproject.iamservice.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.core.utils.TokenUtils;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OrganizationImpl implements Organization {

    private final OrganizationId id;
    private final String name;
    private final Set<ProjectId> projects;
    private final KeyPairData keyPairData;
    private final KeyPairSerialized keyPairSerialized;
    private final Map<String, String> properties;

    public OrganizationImpl(OrganizationId id, String name) throws PKIException {
        this.id = id;
        this.name = name;
        this.projects = new HashSet<>();
        this.keyPairData = TokenUtils.createSelfSignedKeyPairData(id.getId(), ModelUtils.DURATION_10YEARS, TimeUnit.DAYS);
        this.keyPairSerialized = ModelUtils.serializeKeyPair(keyPairData);
        this.properties = new ConcurrentHashMap<>();
    }

    @JsonCreator
    public OrganizationImpl(@JsonProperty("id") OrganizationId id,
                            @JsonProperty("name") String name,
                            @JsonProperty("projects") Collection<ProjectId> projects,
                            @JsonProperty("keyPairSerialized") KeyPairSerialized keyPairSerialized,
                            @JsonProperty("properties") Map<String, String> properties) throws PKIException {
        this.id = id;
        this.name = name;
        this.projects = new HashSet<>();
        projects.forEach(this.projects::add);
        this.keyPairData = ModelUtils.deserializeKeyPair(keyPairSerialized);
        this.keyPairSerialized = keyPairSerialized;
        this.properties = properties;
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
    public void addProject(ProjectId id) {
        projects.add(id);
    }

    @Override
    public Collection<ProjectId> getProjects() {
        return projects.stream().collect(Collectors.toList());
    }

    @Override
    public boolean removeProject(ProjectId id) {
        return projects.remove(id);
    }

    @Override
    public KeyPairSerialized getKeyPairSerialized() {
        return keyPairSerialized;
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
    @JsonIgnore
    public KeyPairData getKeyPairData() {
        return keyPairData;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperty(String key, String value) {
        this.properties.put(key, value);
    }

    @Override
    public void removeProperty(String key) {
        this.properties.remove(key);
    }

}
