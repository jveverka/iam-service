package itx.iamservice.server.tests;

import itx.iamservice.core.model.utils.ModelUtils;
import itx.iamservice.core.services.dto.ClientInfo;
import itx.iamservice.core.services.dto.OrganizationInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.UserInfo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getClientInfo;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getOrganizationInfo;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getOrganizationInfos;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getProjectInfo;
import static itx.iamservice.client.spring.httpclient.HttpClientTestUtils.getUserInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiscoveryAPIsTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void getOrganizationsInfoTest() {
        OrganizationInfo[] organizationInfo = getOrganizationInfos(restTemplate, port);
        assertNotNull(organizationInfo);
        assertTrue(organizationInfo.length > 0);
        assertNotNull(organizationInfo[0]);
        assertNotNull(organizationInfo[0].getName());
        assertNotNull(organizationInfo[0].getOrganizationId());
        assertNotNull(organizationInfo[0].getProjects());
        assertNotNull(organizationInfo[0].getX509Certificate());
    }

    @Test
    @Order(2)
    public void getOrganizationInfoTest() {
        OrganizationInfo organizationInfo = getOrganizationInfo(restTemplate, port, ModelUtils.IAM_ADMINS_ORG);
        assertNotNull(organizationInfo);
        assertNotNull(organizationInfo.getName());
        assertNotNull(organizationInfo.getOrganizationId());
        assertNotNull(organizationInfo.getProjects());
        assertNotNull(organizationInfo.getX509Certificate());
    }

    @Test
    @Order(3)
    public void getProjectInfoTest() {
        ProjectInfo projectInfo = getProjectInfo(restTemplate, port, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT);
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
    @Order(4)
    public void getUserInfoTest() {
        UserInfo userInfo = getUserInfo(restTemplate, port, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, ModelUtils.IAM_ADMIN_USER);
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
    @Order(5)
    public void getClientInfoTest() {
        ClientInfo clientInfo = getClientInfo(restTemplate, port, ModelUtils.IAM_ADMINS_ORG, ModelUtils.IAM_ADMINS_PROJECT, ModelUtils.IAM_ADMIN_CLIENT_ID);
        assertNotNull(clientInfo);
        assertNotNull(clientInfo.getId());
        assertNotNull(clientInfo.getName());
        assertNotNull(clientInfo.getRoles());
        assertNotNull(clientInfo.getPermissions());
    }

}