package itx.iamservice.core.model;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Organization {

    private final OrganizationId id;
    private final String name;
    private final Map<ProjectId, Project> projects;

    public Organization(OrganizationId id, String name) {
        this.id = id;
        this.name = name;
        this.projects = new ConcurrentHashMap<>();
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

    public void remove(ProjectId projectId) {
        projects.remove(projectId);
    }

    public Optional<Project> getProject(ProjectId projectId) {
        return Optional.ofNullable(projects.get(projectId));
    }

}
