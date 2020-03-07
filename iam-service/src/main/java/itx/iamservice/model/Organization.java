package itx.iamservice.model;

import java.util.Collection;
import java.util.stream.Collectors;

public class Organization {

    private final OrganizationId id;
    private final String name;
    private final ModelImpl model;

    public Organization(OrganizationId id, String name, ModelImpl model) {
        this.id = id;
        this.name = name;
        this.model = model;
    }

    public OrganizationId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void add(Project project) {
        model.getProjects().put(project.getId(), project);
    }

    public Collection<Project> getProjects() {
        return model.getProjects().values().stream()
                .filter(project -> project.getOrganizationId().equals(id))
                .collect(Collectors.toList());
    }

    public void remove(ProjectId projectId) {
        model.getProjects().remove(projectId);
    }

}
