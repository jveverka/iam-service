package itx.iamservice.core.model;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Optional;

public interface Organization {

    OrganizationId getId();

    String getName();

    void add(Project project);

    Collection<Project> getProjects();

    boolean remove(ProjectId projectId);

    Optional<Project> getProject(ProjectId projectId);

    PrivateKey getPrivateKey();

    X509Certificate getCertificate();

}
