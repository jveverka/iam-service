package itx.iamservice.model;

import itx.iamservice.model.extensions.authentication.up.UPCredentials;

import java.util.UUID;

public final class ModelUtils {

    private ModelUtils() {
    }

    public static String createId() {
        return UUID.randomUUID().toString();
    }

    public static OrganizationId createOrganizationId() {
        return OrganizationId.from(createId());
    }

    public static ProjectId createProjectId() {
        return ProjectId.from(createId());
    }

    public static ClientId createClientId() {
        return ClientId.from(createId());
    }

    public static RoleId createRoleId() {
        return RoleId.from(createId());
    }

    public static Model createDefaultModel() {
        ModelImpl model = new ModelImpl();
        Organization organization = new Organization(createOrganizationId(), "iam-admins", model);
        Project project = new Project(createProjectId(), "iam-admins", organization.getId(), model);
        Client client = new Client(createClientId(), "iam-admin", project.getId());
        UPCredentials upCredentials = new UPCredentials(client.getId(), "iam-secret-77");
        organization.add(project);
        project.add(client);
        client.addCredentials(upCredentials);
        return model;
    }

}
