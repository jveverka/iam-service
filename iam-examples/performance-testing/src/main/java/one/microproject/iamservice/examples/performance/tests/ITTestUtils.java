package one.microproject.iamservice.examples.performance.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class ITTestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ITTestUtils.class);

    private ITTestUtils() {
    }

    public static final int IAM_SERVER_PORT = 8080;
    public static final String IAM_SERVICE_PROPERTY = "iamservice.url";
    public static final String IAM_ADMIN_PASSWORD_PROPERTY = "admin.pwd";
    public static final String IAM_CLIENT_SECRET_PROPERTY = "client.secret";

    public static URL getDefaultIamServerURL() throws MalformedURLException {
        return new URL("http://localhost:" + IAM_SERVER_PORT);
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

    public static Float scenariosPerSecond(long count, long duration) {
        return count/(duration/1000F);
    }

    public static Float successRatePercent(long total, long success) {
        return (success/((float)total))*100F;
    }

}
