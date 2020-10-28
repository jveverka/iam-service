package itx.iamservice.server.tests;

import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.ClientInfo;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.serviceclient.IAMServiceManagerClient;
import itx.iamservice.serviceclient.IAMServiceClientBuilder;
import itx.iamservice.serviceclient.IAMServiceProjectManagerClient;
import itx.iamservice.serviceclient.IAMServiceUserManagerClient;
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

    private static IAMServiceManagerClient iamServiceManagerClient;
    private static IAMServiceProjectManagerClient iamServiceProjectManagerClient;
    private static IAMServiceUserManagerClient iamServiceUserManagerClient;

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void initTest() {
        String baseUrl = "http://localhost:" + port;
        iamServiceManagerClient = IAMServiceClientBuilder.builder()
                .withBaseUrl(baseUrl)
                .withConnectionTimeout(60L, TimeUnit.SECONDS)
                .build();
        iamServiceProjectManagerClient = iamServiceManagerClient.getIAMServiceProject(null, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
        iamServiceUserManagerClient = iamServiceManagerClient.getIAMServiceUserManagerClient(null, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
    }

    @Test
    @Order(2)
    public void getOrganizationsInfoTest() throws IOException {
        Collection<OrganizationInfo> organizationInfo = iamServiceManagerClient.getOrganizations();
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
        OrganizationInfo organizationInfo = iamServiceManagerClient.getOrganization(ModelUtils.IAM_ADMINS_ORG);
        assertNotNull(organizationInfo);
        assertNotNull(organizationInfo.getName());
        assertNotNull(organizationInfo.getId());
        assertNotNull(organizationInfo.getProjects());
        assertNotNull(organizationInfo.getX509Certificate());
    }

    @Test
    @Order(4)
    public void getProjectInfoTest() throws IOException {
        ProjectInfo projectInfo = iamServiceProjectManagerClient.getInfo();
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
        UserInfo userInfo = iamServiceUserManagerClient.getUserInfo(ModelUtils.IAM_ADMIN_USER);
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
        ClientInfo clientInfo = iamServiceProjectManagerClient.getClientInfo(ModelUtils.IAM_ADMIN_CLIENT_ID);
        assertNotNull(clientInfo);
        assertNotNull(clientInfo.getId());
        assertNotNull(clientInfo.getName());
        assertNotNull(clientInfo.getRoles());
        assertNotNull(clientInfo.getPermissions());
    }

}