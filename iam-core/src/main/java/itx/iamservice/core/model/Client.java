package itx.iamservice.core.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client {

    private final ClientId id;
    private final ProjectId projectId;
    private final String name;
    private final Map<CredentialsType, Credentials> credentials;
    private final Map<RoleId, Role> roles;

    public Client(ClientId id, String name, ProjectId projectId) {
        this.id = id;
        this.name = name;
        this.credentials = new ConcurrentHashMap<>();
        this.roles = new ConcurrentHashMap<>();
        this.projectId = projectId;
    }

    public ClientId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public void addRole(Role role) {
        this.roles.put(role.getId(), role);
    }

    public void addCredentials(Credentials credentials) {
        this.credentials.put(credentials.getType(), credentials);
    }

}
