package one.microproject.iamservice.persistence.mongo.tests;

import one.microproject.iamservice.core.model.Client;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.Project;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.Role;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.keys.ModelKey;
import one.microproject.iamservice.persistence.mongo.MongoUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KeyTransformationTests {

    @Test
    public void testKeySerializationOrganization() {
        ModelKey<Organization> modelKey = ModelKey.from(Organization.class, OrganizationId.from("org-001"));
        String id = MongoUtils.convertToId(modelKey);
        assertNotNull(id);
        assertEquals(1, id.split(MongoUtils.SEPARATOR).length);
    }

    @Test
    public void testKeySerializationProject() {
        ModelKey<Project> modelKey = ModelKey.from(Project.class, OrganizationId.from("org-001"), ProjectId.from("proj-001"));
        String id = MongoUtils.convertToId(modelKey);
        assertNotNull(id);
        assertEquals(2, id.split(MongoUtils.SEPARATOR).length);
    }

    @Test
    public void testKeySerializationUser() {
        ModelKey<User> modelKey = ModelKey.from(User.class, OrganizationId.from("org-001"), ProjectId.from("proj-001"), UserId.from("u-001"));
        String id = MongoUtils.convertToId(modelKey);
        assertNotNull(id);
        assertEquals(3, id.split(MongoUtils.SEPARATOR).length);
    }

    @Test
    public void testKeySerializationClient() {
        ModelKey<Client> modelKey = ModelKey.from(Client.class, OrganizationId.from("org-001"), ProjectId.from("proj-001"), ClientId.from("c-001"));
        String id = MongoUtils.convertToId(modelKey);
        assertNotNull(id);
        assertEquals(3, id.split(MongoUtils.SEPARATOR).length);
    }

    @Test
    public void testKeySerializationRole() {
        ModelKey<Role> modelKey = ModelKey.from(Role.class, OrganizationId.from("org-001"), ProjectId.from("proj-001"), RoleId.from("c-001"));
        String id = MongoUtils.convertToId(modelKey);
        assertNotNull(id);
        assertEquals(3, id.split(MongoUtils.SEPARATOR).length);
    }

}
