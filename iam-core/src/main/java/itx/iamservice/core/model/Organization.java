package itx.iamservice.core.model;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Organization {

    private final OrganizationId id;
    private final String name;
    private final Map<ProjectId, Project> projects;
    private final KeyPair keyPair;
    private final X509Certificate certificate;

    public Organization(OrganizationId id, String name) throws PKIException {
        this.id = id;
        this.name = name;
        this.projects = new ConcurrentHashMap<>();
        PKIData pkiData = TokenUtils.createSelfSignedPKIData(id.getId(), 365L, TimeUnit.DAYS);
        this.keyPair = pkiData.getKeyPair();
        this.certificate = pkiData.getX509Certificate();
    }

    public OrganizationId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void add(Project project) {
        projects.put(project.getId(), project);
    }

    public Collection<Project> getProjects() {
        return projects.values().stream()
                .filter(project -> project.getOrganizationId().equals(id))
                .collect(Collectors.toList());
    }

    public boolean remove(ProjectId projectId) {
        return projects.remove(projectId) != null;
    }

    public Optional<Project> getProject(ProjectId projectId) {
        return Optional.ofNullable(projects.get(projectId));
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

}
