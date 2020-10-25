package itx.iamservice.server.tests;

import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.ClientInfo;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.serviceclient.IAMServiceClient;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import itx.iamservice.serviceclient.IAMServiceProject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiscoveryAPIsTests {

    private static IAMServiceClient iamServiceClient;
    private static IAMServiceProject iamServiceProject;

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void initTest() {
        String baseUrl = "http://localhost:" + port;
        iamServiceClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        iamServiceProject = iamServiceClient.getIAMServiceProject(null, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
    }

    @Test
    @Order(2)
    public void getOrganizationsInfoTest() throws IOException {
        Collection<OrganizationInfo> organizationInfo = iamServiceClient.getOrganizations();
        assertNotNull(organizationInfo);
        assertTrue(organizationInfo.size() > 0);
        assertNotNull(organizationInfo);
        Optional<OrganizationInfo> first = organizationInfo.stream().findFirst();
        assertNotNull(first.get().getName());
        assertNotNull(first.get().getId());
        assertNotNull(first.get().getProjects());
        assertNotNull(first.get().getX509Certificate());
    }

    @Test
    @Order(3)
    public void getOrganizationInfoTest() throws IOException {
        OrganizationInfo organizationInfo = iamServiceClient.getOrganization(ModelUtils.IAM_ADMINS_ORG);
        assertNotNull(organizationInfo);
        assertNotNull(organizationInfo.getName());
        assertNotNull(organizationInfo.getId());
        assertNotNull(organizationInfo.getProjects());
        assertNotNull(organizationInfo.getX509Certificate());
    }

    @Test
    @Order(4)
    public void getProjectInfoTest() throws IOException {
        ProjectInfo projectInfo = iamServiceProject.getInfo();
        assertNotNull(projectInfo);
        assertNotNull(projectInfo.getId());
        assertNotNull(projectInfo.getName());
        assertNotNull(projectInfo.getUsers());
        assertNotNull(projectInfo.getClients());
        assertNotNull(projectInfo.getOrganizationCertificate());
        assertNotNull(projectInfo.getOrganizationId());
        assertNotNull(projectInfo.getProjectCertificate());
    }

    @Test
    @Order(5)
    public void getUserInfoTest() throws IOException {
        UserInfo userInfo = iamServiceProject.getUserInfo(ModelUtils.IAM_ADMIN_USER);
        assertNotNull(userInfo);
        assertNotNull(userInfo.getId());
        assertNotNull(userInfo.getName());
        assertNotNull(userInfo.getRoles());
        assertNotNull(userInfo.getOrganizationId());
        assertNotNull(userInfo.getOrganizationCertificate());
        assertNotNull(userInfo.getProjectCertificate());
        assertNotNull(userInfo.getUserCertificate());
        assertNotNull(userInfo.getProjectId());
    }

    @Test
    @Order(6)
    public void getClientInfoTest() throws IOException {
        ClientInfo clientInfo = iamServiceProject.getClientInfo(ModelUtils.IAM_ADMIN_CLIENT_ID);
        assertNotNull(clientInfo);
        assertNotNull(clientInfo.getId());
        assertNotNull(clientInfo.getName());
        assertNotNull(clientInfo.getRoles());
        assertNotNull(clientInfo.getPermissions());
    }

}