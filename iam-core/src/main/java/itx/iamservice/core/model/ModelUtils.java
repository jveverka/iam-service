package itx.iamservice.core.model;

import itx.iamservice.core.model.extensions.authentication.up.UPCredentials;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class ModelUtils {

    private static final String IAM_ADMINS_NAME = "iam-admins";
    public static final OrganizationId IAM_ADMINS_ORG = OrganizationId.from(IAM_ADMINS_NAME);
    public static final ProjectId IAM_ADMINS_PROJECT = ProjectId.from(IAM_ADMINS_NAME);
    public static final ClientId IAM_ADMIN_CLIENT = ClientId.from("iam-admin-id");

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

    public static Model createDefaultModel(String iamAdminPassword) throws NoSuchAlgorithmException {
        ModelImpl model = new ModelImpl();
        Organization organization = new Organization(IAM_ADMINS_ORG, IAM_ADMINS_NAME);
        Project project = new Project(IAM_ADMINS_PROJECT, IAM_ADMINS_NAME, organization.getId());
        Client client = new Client(IAM_ADMIN_CLIENT, "iam-admin", project.getId(), TokenUtils.generateKeyPair(), 3600*1000L);
        UPCredentials upCredentials = new UPCredentials(client.getId(), iamAdminPassword);
        client.addCredentials(upCredentials);
        organization.add(project);
        project.add(client);
        model.add(organization);
        return model;
    }

}
