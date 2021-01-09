package one.microproject.iamservice.examples.ittests;

import one.microproject.iamservice.core.dto.TokenResponseWrapper;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.utils.ModelUtils;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final  class ITTestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ITTestUtils.class);

    private ITTestUtils() {
    }

    public static final int IAM_SERVER_PORT = 8080;
    public static final String IAM_SERVICE_PROPERTY = "iamservice.url";
    public static final String IAM_ADMIN_PASSWORD_PROPERTY = "admin.pwd";
    public static final String IAM_CLIENT_SECRET_PROPERTY = "client.secret";

    public static final OrganizationId organizationId = OrganizationId.from("it-testing-001");
    public static final ProjectId projectId = ProjectId.from("spring-method-security");
    public static final UserId appAdminUserId = UserId.from("user-001");
    public static final ClientId clientId = ClientId.from("client-001");

    public static URL getDefaultIamServerURL() throws MalformedURLException {
        return new URL("http://localhost:" + IAM_SERVER_PORT);
    }

    public static TokenResponseWrapper getGlobalAdminTokens(IAMServiceManagerClient iamServiceManagerClient) throws IOException {
        return iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
    }

    public static TokenResponseWrapper getGlobalAdminTokens(IAMServiceManagerClient iamServiceManagerClient, String adminPassword, String clientSecret) throws IOException {
        return iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", adminPassword, ModelUtils.IAM_ADMIN_CLIENT_ID, clientSecret);
    }

    public static String getGlobalAdminPassword() {
        String adminPassword = System.getProperty(IAM_ADMIN_PASSWORD_PROPERTY);
        if (adminPassword != null && !"".equals(adminPassword)) {
            return adminPassword;
        }
        LOG.info("using default IAM Admin password");
        return "secret";
    }

    public static String getGlobalAdminClientSecret() {
        String clientSecret = System.getProperty(IAM_CLIENT_SECRET_PROPERTY);
        if (clientSecret != null && !"".equals(clientSecret)) {
            return clientSecret;
        }
        LOG.info("using default IAM Client secret");
        return "top-secret";
    }

    public static URL getIAMServiceURL() throws MalformedURLException {
        try {
            String iamServerURL = System.getProperty(IAM_SERVICE_PROPERTY);
            if (iamServerURL != null) {
                return new URL(iamServerURL);
            }
        } catch (MalformedURLException e) {
            LOG.debug("ERROR: ", e);
        }
        LOG.info("using default IAM-Service URL");
        return getDefaultIamServerURL();
    }

    public static String getIssuerURI(String baseURL, OrganizationId organizationId, ProjectId projectId) {
        return baseURL + "/services/oauth2/" + organizationId.getId() + "/" + projectId.getId();
    }

}
