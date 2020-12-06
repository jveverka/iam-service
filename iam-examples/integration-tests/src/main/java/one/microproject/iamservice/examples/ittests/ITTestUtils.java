package one.microproject.iamservice.examples.ittests;

import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.model.utils.ModelUtils;
import one.microproject.iamservice.serviceclient.IAMServiceManagerClient;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;

import java.net.MalformedURLException;
import java.net.URL;

public final  class ITTestUtils {

    private ITTestUtils() {
    }

    public final static int iamServerPort = 8080;
    public final static String IAM_SERVICE_PROPERTY = "iamservice.url";

    public final static OrganizationId organizationId = OrganizationId.from("it-testing-001");
    public final static ProjectId projectId = ProjectId.from("spring-method-security");
    public final static UserId appAdminUserId = UserId.from("user-001");
    public final static ClientId clientId = ClientId.from("client-001");

    public static URL getDefaultIamServerURL() throws MalformedURLException {
        return new URL("http://localhost:" + iamServerPort);
    }

    public static TokenResponse getIAMAdminTokens(IAMServiceManagerClient iamServiceManagerClient) throws AuthenticationException {
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
            //e.printStackTrace();
        }
        return getDefaultIamServerURL();
    }

}
