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

    public static final OrganizationId organizationId = OrganizationId.from("it-testing-001");
    public static final ProjectId projectId = ProjectId.from("spring-method-security");
    public static final UserId appAdminUserId = UserId.from("user-001");
    public static final ClientId clientId = ClientId.from("client-001");

    public static URL getDefaultIamServerURL() throws MalformedURLException {
        return new URL("http://localhost:" + IAM_SERVER_PORT);
    }

    public static TokenResponseWrapper getIAMAdminTokens(IAMServiceManagerClient iamServiceManagerClient) throws IOException {
        return iamServiceManagerClient
                .getIAMAdminAuthorizerClient()
                .getAccessTokensOAuth2UsernamePassword("admin", "secret", ModelUtils.IAM_ADMIN_CLIENT_ID, "top-secret");
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

}
