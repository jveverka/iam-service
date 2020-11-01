package one.microproject.iamservice.core.model.tests;

import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static one.microproject.iamservice.core.ModelCommons.ACTION_ALL;
import static one.microproject.iamservice.core.ModelCommons.CLIENTS_RESOURCE;
import static one.microproject.iamservice.core.ModelCommons.createProjectAdminPermissions;
import static one.microproject.iamservice.core.ModelCommons.verifyProjectAdminPermissions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectAdminPermissionsTests {

    private static final OrganizationId organizationId = OrganizationId.from("org-999");
    private static final ProjectId projectId = ProjectId.from("proj-999");

    private static Set<String> projectAdminScopes;

    @BeforeAll
    private static void init() {
        projectAdminScopes = createProjectAdminPermissions(organizationId, projectId)
                .stream().map(p->p.asStringValue()).collect(Collectors.toSet());;
    }

    @Test
    public void testMinimalValidProjectPermissions() {
        boolean result = verifyProjectAdminPermissions(organizationId, projectId, projectAdminScopes);
        assertTrue(result);
    }

    @Test
    public void testValidProjectPermissions() {
        Set<String> extendedScopes = new HashSet<>(projectAdminScopes);
        extendedScopes.add("service.resource.action");
        boolean result = verifyProjectAdminPermissions(organizationId, projectId, extendedScopes);
        assertTrue(result);
    }

    @Test
    public void testInValidProjectPermissions() {
        Set<String> extendedScopes = new HashSet<>();
        extendedScopes.add("service.resource.action");
        boolean result = verifyProjectAdminPermissions(organizationId, projectId, extendedScopes);
        assertFalse(result);
    }

    @Test
    public void testInsufficientValidProjectPermissions() {
        Set<String> extendedScopes = new HashSet<>(projectAdminScopes);
        extendedScopes.remove(organizationId.getId() + "-" + projectId.getId() + "." + CLIENTS_RESOURCE + "." + ACTION_ALL);
        boolean result = verifyProjectAdminPermissions(organizationId, projectId, extendedScopes);
        assertFalse(result);
    }

}
